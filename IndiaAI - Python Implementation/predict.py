import tkinter as tk
from tkinter import ttk, messagebox
import joblib
import numpy as np
from keras.models import load_model
from sklearn.feature_extraction.text import TfidfVectorizer
from symspellpy import SymSpell, Verbosity
import nltk

# Download required NLTK models
nltk.download('punkt')
max_edit_distance_dictionary = 2  # Maximum edit distance for lookups
prefix_length = 7  # Length of prefixes used for dictionary entries
sym_spell = SymSpell(max_edit_distance_dictionary, prefix_length)

# Load a dictionary file (pre-built dictionary for faster processing)
# Download frequency_dictionary_en_82_765.txt from https://github.com/mammothb/symspellpy
dictionary_path = "frequency_dictionary_en_82_765.txt"
term_index = 0  # Column of the term in the dictionary
count_index = 1  # Column of the term frequency
sym_spell.load_dictionary(dictionary_path, term_index, count_index)


# Load encoders and vectorizer
category_encoder = joblib.load('category_encoder.pkl')
subcategory_encoder = joblib.load('subcategory_encoder.pkl')
tfidf_vectorizer = joblib.load('TFIDF_vectorizer.pkl')

# Domain entities dictionary
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

entity_mapping = {word: sub_category for sub_category, words in domain_entities.items() for word in words}

# Function to predict category and subcategory
def predict_complaint():
    complaint_text = complaint_input.get("1.0", tk.END).strip()
    if not complaint_text:
        messagebox.showerror("Error", "Please enter a complaint.")
        return

    # Correct spelling
    corrected_text = sym_spell.lookup_compound(complaint_text, max_edit_distance=2)
    corrected_text = corrected_text[0].term if corrected_text else complaint_text

    # Extract tokens
    tokens = nltk.word_tokenize(corrected_text)

    # Filter tokens based on domain entities
    relevant_tokens = [token for token in tokens if token.lower() in entity_mapping]
    explanation_text.set(f"Relevant Tokens: {', '.join(relevant_tokens)}")

    # TF-IDF transformation
    complaint_tfidf = tfidf_vectorizer.transform([' '.join(tokens)])

    # Model selection
    selected_model = model_choice.get()
    if selected_model == "LGBM":
        # Load models
        subcategory_model = joblib.load('LGBM_subcategory_model.pkl')
        category_model = joblib.load('LGBM_category_model.pkl')

        # Predict subcategory
        subcategory_pred = subcategory_model.predict(complaint_tfidf)
        subcategory_name = subcategory_encoder.inverse_transform(subcategory_pred)[0]

        # Add subcategory prediction as a feature for category prediction
        category_features = np.hstack([complaint_tfidf.toarray(), subcategory_pred.reshape(-1, 1)])
        category_pred = category_model.predict(category_features)
        category_name = category_encoder.inverse_transform(category_pred)[0]
    elif selected_model == "LSTM":
        # Load models
        subcategory_model = load_model('LSTM_subcategory_model.h5')
        category_model = load_model('LSTM_category_model.h5')

        # Predict subcategory
        subcategory_proba = subcategory_model.predict(complaint_tfidf.toarray())
        subcategory_pred = np.argmax(subcategory_proba, axis=1)
        subcategory_name = subcategory_encoder.inverse_transform(subcategory_pred)[0]

        # Add subcategory prediction as a feature for category prediction
        category_features = np.hstack([complaint_tfidf.toarray(), subcategory_pred.reshape(-1, 1)])
        category_proba = category_model.predict(category_features)
        category_pred = np.argmax(category_proba, axis=1)
        category_name = category_encoder.inverse_transform(category_pred)[0]
    else:
        messagebox.showerror("Error", "Please select a model.")
        return

    # Display predictions
    category_result.set(f"Category: {category_name}")
    subcategory_result.set(f"Subcategory: {subcategory_name}")

# GUI Setup
root = tk.Tk()
root.title("Complaint Classification")
root.geometry("600x500")
root.configure(bg="#f5f5f5")

# Title Label
title_label = tk.Label(root, text="Complaint Classification System", font=("Helvetica", 16, "bold"), bg="#f5f5f5")
title_label.pack(pady=10)

# Model Selection
model_frame = tk.Frame(root, bg="#f5f5f5")
model_frame.pack(pady=10)
model_label = tk.Label(model_frame, text="Select Model:", font=("Helvetica", 12), bg="#f5f5f5")
model_label.grid(row=0, column=0, padx=5)
model_choice = ttk.Combobox(model_frame, values=["LGBM", "LSTM"], state="readonly")
model_choice.grid(row=0, column=1, padx=5)
model_choice.set("LGBM")  # Default selection

# Complaint Input
input_label = tk.Label(root, text="Enter Complaint:", font=("Helvetica", 12), bg="#f5f5f5")
input_label.pack(anchor="w", padx=20, pady=5)
complaint_input = tk.Text(root, height=5, width=60, wrap="word", font=("Helvetica", 12))
complaint_input.pack(padx=20, pady=5)

# Predict Button
predict_button = tk.Button(root, text="Predict", font=("Helvetica", 12), bg="#4caf50", fg="white", command=predict_complaint)
predict_button.pack(pady=10)

# Prediction Results
result_frame = tk.Frame(root, bg="#f5f5f5")
result_frame.pack(pady=10)
category_result = tk.StringVar()
subcategory_result = tk.StringVar()
explanation_text = tk.StringVar()
category_label = tk.Label(result_frame, textvariable=category_result, font=("Helvetica", 12), bg="#f5f5f5")
category_label.pack(anchor="w", padx=20)
subcategory_label = tk.Label(result_frame, textvariable=subcategory_result, font=("Helvetica", 12), bg="#f5f5f5")
subcategory_label.pack(anchor="w", padx=20)
explanation_label = tk.Label(result_frame, textvariable=explanation_text, font=("Helvetica", 10, "italic"), bg="#f5f5f5")
explanation_label.pack(anchor="w", padx=20, pady=5)

# Run GUI
root.mainloop()
