
# Complaint Classification System

This repository contains a complaint classification system that categorizes complaints into predefined categories and subcategories using machine learning models. The system includes preprocessing, training, and prediction components.

## Features

- **Preprocessing Pipeline**: Cleans and processes raw complaint data, including spelling correction, tokenization, and POS tagging.
- **Model Training**: Supports training LightGBM (LGBM) and LSTM models for category and subcategory classification.
- **Prediction System**: Classifies complaints in real time using the trained models.
- **Explainable Predictions**: Displays relevant tokens extracted from complaints for better interpretability.

## Directory Structure

```
.
├── preprocessing.py       # Data preprocessing pipeline
├── train.py               # Model training script
├── predict.py             # GUI-based complaint classification system
├── README.md              # Project documentation
├── requirements.txt       # Python dependencies
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/complaint-classification-system.git
   cd complaint-classification-system
   ```

2. Install the required Python packages:
   ```bash
   pip install -r requirements.txt
   ```

## Usage

### Preprocessing

Run the preprocessing pipeline to clean and prepare your data:
```bash
python preprocessing.py --train path/to/train.csv --test path/to/test.csv
```

### Training

Train the model by specifying the desired algorithm (`LGBM` or `LSTM`):
```bash
python train.py --train path/to/train.csv --test path/to/test.csv --model LGBM
```

### Prediction
Download Model Weights: To use the trained model for prediction, you need to download the model weights from this link.
https://drive.google.com/file/d/1D9QAE17p90szQi8si0I4j1ftIAJinJCQ/view?usp=sharing
Move the downloaded model weights file into the model_weights/ directory
Launch the GUI-based prediction system:
```bash
python predict.py
```

- Enter a complaint in the text area.
- Select a model (`LGBM` or `LSTM`).
- Click "Predict" to classify the complaint into a category and subcategory.

## Dependencies

Install the required libraries listed in `requirements.txt`:
```text
joblib
numpy
pandas
tqdm
nltk
keras
scikit-learn
symspellpy
lightgbm
```

## File Details

- **`preprocessing.py`**:
  - Handles missing values, spelling correction, tokenization, and tagging.
  - Saves the processed data as CSV files for model training.

- **`train.py`**:
  - Generates TF-IDF features and encodes labels.
  - Trains models for category and subcategory classification using LGBM or LSTM.
  - Saves the trained models for later use.

- **`predict.py`**:
  - Implements a GUI for complaint classification.
  - Provides explainable predictions by highlighting relevant tokens.

## Example Dataset

The input data should include the following columns:
- `crimeaditionalinfo`: The text of the complaint.
- `category`: The category label (for training).
- `sub_category`: The subcategory label (for training).

## Future Enhancements

- Add support for semi-supervised learning to improve accuracy with limited data.
- Implement synthetic data generation to handle class imbalance.

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for suggestions and improvements.

## License

This project is licensed under the [MIT License](LICENSE).
