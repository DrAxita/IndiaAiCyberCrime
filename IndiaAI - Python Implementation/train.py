import argparse
import pandas as pd
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import classification_report, accuracy_score
from lightgbm import LGBMClassifier
from keras.models import Sequential
from keras.layers import Dense, Dropout
from keras.utils import to_categorical
from scipy.sparse import hstack
import joblib
from preprocessing import preprocess_pipeline  # Import from preprocessing.py
import pandas as pd
import os
from tqdm import tqdm
def preprocess_tokens(tokens_pos):
    if isinstance(tokens_pos, str):
        return tokens_pos  # If already a string
    elif isinstance(tokens_pos, list):
        return ' '.join(tokens_pos)  # Convert list of tokens to a space-separated string
    else:
        return ''  # Handle edge cases (e.g., NaN)

# ------------------------------------------------------------
# Feature Engineering
# ------------------------------------------------------------
def generate_features(train_file, test_file):
    """
    Generate TF-IDF features and encode labels from training and test datasets.
    """
    import pandas as pd
    from sklearn.feature_extraction.text import TfidfVectorizer
    from sklearn.preprocessing import LabelEncoder
    import joblib

    # Read the input files
    train_df = pd.read_csv(train_file)
    test_df = pd.read_csv(test_file)

    

    # Initialize vectorizer and encoders
    tfidf_vectorizer = TfidfVectorizer(max_features=5000)
    category_encoder = LabelEncoder()
    subcategory_encoder = LabelEncoder()
    train_df['processed_tokens'] = train_df['filtered_tokens'].apply(preprocess_tokens)
    test_df['processed_tokens'] = test_df['filtered_tokens'].apply(preprocess_tokens)
    # TF-IDF transformation
    print("Generating TF-IDF features...")
    X_train_text = tfidf_vectorizer.fit_transform(train_df['processed_tokens'])
    X_test_text = tfidf_vectorizer.transform(test_df['processed_tokens'])

    # Encode labels for category and subcategory
    print("Encoding labels...")
    y_train_category = category_encoder.fit_transform(train_df['category'])
    y_test_category = category_encoder.transform(test_df['category'])
    y_train_subcategory = subcategory_encoder.fit_transform(train_df['sub_category'])
    y_test_subcategory = subcategory_encoder.transform(test_df['sub_category'])

    # Save encoders and vectorizer for later use
    print("Saving TF-IDF vectorizer and encoders...")
    joblib.dump(tfidf_vectorizer, 'TFIDF_vectorizer.pkl')
    joblib.dump(category_encoder, 'category_encoder.pkl')
    joblib.dump(subcategory_encoder, 'subcategory_encoder.pkl')
    print("Saved: 'TFIDF_vectorizer.pkl', 'category_encoder.pkl', 'subcategory_encoder.pkl'")

    # Return features and labels
    return (X_train_text, X_test_text, y_train_category, y_test_category, 
            y_train_subcategory, y_test_subcategory)


# ------------------------------------------------------------
# Train LGBM Models
# ------------------------------------------------------------
def train_lgbm(X_train_text, X_test_text, y_train_category, y_test_category, y_train_subcategory, y_test_subcategory):
    """
    Train LGBM models for subcategory and category.
    """
    # Train subcategory model
    subcategory_model = LGBMClassifier(random_state=42)
    subcategory_model.fit(X_train_text, y_train_subcategory)
    y_pred_subcategory = subcategory_model.predict(X_test_text)

    print("Subcategory Classification Report:\n", 
          classification_report(y_test_subcategory, y_pred_subcategory))

    # Add predicted subcategories as features
    X_train_combined = hstack([X_train_text, y_train_subcategory.reshape(-1, 1)])
    X_test_combined = hstack([X_test_text, y_pred_subcategory.reshape(-1, 1)])

    # Train category model
    category_model = LGBMClassifier(random_state=42)
    category_model.fit(X_train_combined, y_train_category)
    y_pred_category = category_model.predict(X_test_combined)

    print("Category Classification Report:\n", 
          classification_report(y_test_category, y_pred_category))

    # Save models
    joblib.dump(subcategory_model, 'LGBM_subcategory_model.pkl')
    joblib.dump(category_model, 'LGBM_category_model.pkl')
    print("LGBM Models saved successfully.")

# ------------------------------------------------------------
# Train LSTM Models
# ------------------------------------------------------------
def train_lstm(X_train_text, X_test_text, y_train_category, y_test_category, y_train_subcategory, y_test_subcategory):
    """
    Train LSTM models for subcategory and category.
    """
    # Convert sparse matrices to dense arrays
    X_train_dense = X_train_text.toarray()
    X_test_dense = X_test_text.toarray()

    # One-hot encode labels
    y_train_subcategory_encoded = to_categorical(y_train_subcategory)
    y_test_subcategory_encoded = to_categorical(y_test_subcategory)
    y_train_category_encoded = to_categorical(y_train_category)
    y_test_category_encoded = to_categorical(y_test_category)

    # Subcategory model
    subcategory_model = Sequential([
        Dense(256, activation='relu', input_shape=(X_train_dense.shape[1],)),
        Dropout(0.2),
        Dense(128, activation='relu'),
        Dense(y_train_subcategory_encoded.shape[1], activation='softmax')
    ])
    subcategory_model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
    subcategory_model.fit(X_train_dense, y_train_subcategory_encoded, epochs=25, batch_size=32, validation_data=(X_test_dense, y_test_subcategory_encoded))
    subcategory_model.save('LSTM_subcategory_model.h5')
    print("LSTM Subcategory Model saved successfully.")

    # Add predicted subcategories as features
    y_pred_subcategory = subcategory_model.predict(X_test_dense).argmax(axis=1)
    X_train_combined = np.hstack([X_train_dense, y_train_subcategory.reshape(-1, 1)])
    X_test_combined = np.hstack([X_test_dense, y_pred_subcategory.reshape(-1, 1)])

    # Category model
    category_model = Sequential([
        Dense(256, activation='relu', input_shape=(X_train_combined.shape[1],)),
        Dropout(0.2),
        Dense(128, activation='relu'),
        Dense(len(y_train_category_encoded[0]), activation='softmax')
    ])
    category_model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
    category_model.fit(X_train_combined, y_train_category_encoded, epochs=25, batch_size=32, validation_data=(X_test_combined, y_test_category_encoded))
    category_model.save('LSTM_category_model.h5')
    print("LSTM Category Model saved successfully.")

# ------------------------------------------------------------
# Main Script
# ------------------------------------------------------------


def main():
    # Parse command-line arguments
    parser = argparse.ArgumentParser(description="Train category and subcategory models.")
    parser.add_argument("--train", required=True, help="Path to the raw training file.")
    parser.add_argument("--test", required=True, help="Path to the raw test file.")
    parser.add_argument("--model", choices=["LGBM", "LSTM"], required=True, help="Model type to train (LGBM or LSTM).")
    parser.add_argument("--processed_train", default="processed_train.csv", help="Path to save processed training data.")
    parser.add_argument("--processed_test", default="processed_test.csv", help="Path to save processed test data.")
    args = parser.parse_args()

    # Check if processed files already exist
    if os.path.exists(args.processed_train) and os.path.exists(args.processed_test):
        print("Processed files found. Skipping preprocessing...")
    else:
        print("Processed files not found. Running preprocessing pipeline...")
        preprocess_pipeline(args.train, args.test, args.processed_train, args.processed_test)

    # Generate features
    print("Generating features...")
    (X_train_text, X_test_text, y_train_category, y_test_category, 
     y_train_subcategory, y_test_subcategory) = generate_features(args.processed_train, args.processed_test)

    # Train the chosen model
    if args.model == "LGBM":
        print("Training LGBM model...")
        train_lgbm(X_train_text, X_test_text, y_train_category, y_test_category, y_train_subcategory, y_test_subcategory)
    elif args.model == "LSTM":
        print("Training LSTM model...")
        train_lstm(X_train_text, X_test_text, y_train_category, y_test_category, y_train_subcategory, y_test_subcategory)

if __name__ == "__main__":
    main()
