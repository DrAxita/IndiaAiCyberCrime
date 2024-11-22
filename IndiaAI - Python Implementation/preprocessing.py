import pandas as pd
from joblib import Parallel, delayed
import nltk
from nltk.tokenize import word_tokenize
from symspellpy import SymSpell, Verbosity
from tqdm import tqdm
def preprocess_pipeline(train_file, test_file, output_train, output_test):
    """
    Preprocess training and test datasets and handle unseen categories/subcategories.
    """
    # Step 1: Read files and convert encoding
    train_df = pd.read_csv(train_file, encoding='latin1')
    test_df = pd.read_csv(test_file, encoding='latin1')
    train_df.to_csv('train_utf8.csv', index=False, encoding='utf-8')
    test_df.to_csv('test_utf8.csv', index=False, encoding='utf-8')
    train_df = pd.read_csv('train_utf8.csv')
    test_df = pd.read_csv('test_utf8.csv')

    # Step 2: Handle missing values
    train_df = train_df.dropna(subset=['crimeaditionalinfo'])
    train_df['sub_category'] = train_df['sub_category'].fillna('Unknown')

    # Step 3: Debugging Code for Unseen Categories
    unseen_categories = set(test_df['category']) - set(train_df['category'])
    unseen_subcategories = set(test_df['sub_category']) - set(train_df['sub_category'])
    print("Unseen Categories in Test Data:", unseen_categories)
    print("Unseen Subcategories in Test Data:", unseen_subcategories)

    unseen_category_rows = test_df[test_df['category'].isin(unseen_categories)]
    unseen_subcategory_rows = test_df[test_df['sub_category'].isin(unseen_subcategories)]
    print("Rows with unseen categories:\n", unseen_category_rows)
    print("Rows with unseen subcategories:\n", unseen_subcategory_rows)

    # Step 4: Remove rows with unseen labels
    test_df = test_df[
        ~test_df['category'].isin(unseen_categories) &
        ~test_df['sub_category'].isin(unseen_subcategories)
    ]
    print(f"Updated Test Dataset Size After Removing Unseen Labels: {test_df.shape}")
    max_edit_distance_dictionary = 2  # Maximum edit distance for lookups
    prefix_length = 7  # Length of prefixes used for dictionary entries
    sym_spell = SymSpell(max_edit_distance_dictionary, prefix_length)

# Load a dictionary file (pre-built dictionary for faster processing)
# Download frequency_dictionary_en_82_765.txt from https://github.com/mammothb/symspellpy
    dictionary_path = "frequency_dictionary_en_82_765.txt"
    term_index = 0  # Column of the term in the dictionary
    count_index = 1  # Column of the term frequency
    sym_spell.load_dictionary(dictionary_path, term_index, count_index)

    # Step 5: Spelling correction
    def correct_spelling(text):
        if isinstance(text, str):
            suggestions = sym_spell.lookup_compound(text, max_edit_distance=2)
            if suggestions:
                return suggestions[0].term
        return text

    tqdm.pandas()
    train_df['corrected_text'] = train_df['crimeaditionalinfo'].progress_apply(correct_spelling)
    test_df['corrected_text'] = test_df['crimeaditionalinfo'].progress_apply(correct_spelling)

    # Step 6: Tokenization
    def tokenize_text(text):
        return word_tokenize(text) if isinstance(text, str) else []

    train_df['tokens'] = train_df['corrected_text'].progress_apply(tokenize_text)
    test_df['tokens'] = test_df['corrected_text'].progress_apply(tokenize_text)

    # Step 7: POS tagging
    def pos_tag_tokens(tokens):
        return nltk.pos_tag(tokens)

    train_df['pos_tags'] = train_df['tokens'].progress_apply(pos_tag_tokens)
    test_df['pos_tags'] = test_df['tokens'].progress_apply(pos_tag_tokens)

    # Define POS tags to keep
    important_pos_tags = {'NN', 'NNS', 'NNP', 'NNPS', 'VB', 'VBD', 'VBG', 'VBN', 'VBP', 'VBZ', 'JJ', 'JJR', 'JJS', 'RB', 'RBR', 'RBS'}
    # Step 8: Filter by POS tags
    def filter_by_pos(pos_tags):
        return [word for word, tag in pos_tags if tag in important_pos_tags]

    train_df['filtered_tokens'] = train_df['pos_tags'].progress_apply(filter_by_pos)
    test_df['filtered_tokens'] = test_df['pos_tags'].progress_apply(filter_by_pos)
    # Domain Entities Dictionary
    domain_entities = {
    'Business Email CompromiseEmail Takeover': ['email', 'takeover', 'business'],
    'Cheating by Impersonation': ['cheating', 'impersonation', 'fraud'],
    'Cryptocurrency Fraud': ['cryptocurrency', 'bitcoin', 'crypto', 'fraud'],
    'Cyber Bullying  Stalking  Sexting': ['bullying', 'stalking', 'sexting', 'harassment'],
    'Cyber Terrorism': ['terrorism', 'cyber', 'extremism'],
    'Damage to computer computer systems etc': ['damage', 'computer', 'systems'],
    'Data Breach/Theft': ['data', 'breach', 'theft', 'hacking'],
    'DebitCredit Card FraudSim Swap Fraud': ['card', 'debit', 'credit', 'fraud', 'sim'],
    'Denial of Service (DoS)/Distributed Denial of Service (DDOS) attacks': ['ddos', 'dos', 'denial'],
    'EWallet Related Fraud': ['ewallet', 'fraud', 'wallet', 'money'],
    'Email Hacking': ['email', 'hacking', 'phishing'],
    'FakeImpersonating Profile': ['fake', 'impersonating', 'profile'],
    'Fraud CallVishing': ['fraud', 'call', 'vishing'],
    'Hacking/Defacement': ['hacking', 'defacement'],
    'Online Gambling  Betting': ['gambling', 'betting', 'online'],
    'Online Job Fraud': ['job', 'fraud', 'scam'],
    'Online Matrimonial Fraud': ['matrimonial', 'marriage', 'fraud'],
    'Ransomware': ['ransomware', 'attack', 'encryption'],
    'UPI Related Frauds': ['upi', 'fraud', 'transaction', 'money'],
    'Website DefacementHacking': ['website', 'hacking', 'defacement']
    }

    # Flatten the dictionary for NER tagging
    entity_mapping = {word: sub_category for sub_category, words in domain_entities.items() for word in words}

    # Step 9: Domain-based tagging
    def tag_domain_entities(tokens):
        return [(token, entity_mapping.get(token.lower(), 'O')) for token in tokens]

    def parallel_apply(df, func, column):
        return Parallel(n_jobs=-1)(delayed(func)(tokens) for tokens in df[column])

    use_parallel = True
    if use_parallel:
        train_df['domain_tags'] = parallel_apply(train_df, tag_domain_entities, 'filtered_tokens')
        test_df['domain_tags'] = parallel_apply(test_df, tag_domain_entities, 'filtered_tokens')
    else:
        train_df['domain_tags'] = train_df['filtered_tokens'].progress_apply(tag_domain_entities)
        test_df['domain_tags'] = test_df['filtered_tokens'].progress_apply(tag_domain_entities)

    # Step 10: Save processed files
    train_df.to_csv(output_train, index=False)
    test_df.to_csv(output_test, index=False)
    print("Preprocessing completed. Processed data saved:")
    print(f"- Training data: {output_train}")
    print(f"- Test data: {output_test}")
