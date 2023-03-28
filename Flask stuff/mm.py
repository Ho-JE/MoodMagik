#Import data packages
import os
import sys
import glob
import numpy as np
import pandas as pd

#Import audio packages
import librosa
import librosa.display
from scipy.io import wavfile
import scipy.io.wavfile
import sys

#Import plotting packages
import matplotlib.pyplot as plt
from matplotlib.pyplot import specgram
import matplotlib.pyplot as plt
import seaborn as sns
#Import Keras & Tensorflow packages
import keras
from keras import regularizers
from keras.preprocessing import sequence
from keras.models import Sequential
from keras.layers import Dense, Embedding
from keras.layers import LSTM
from keras.preprocessing.text import Tokenizer
from keras_preprocessing.sequence import pad_sequences
from keras.utils import to_categorical
from keras.layers import Input, Flatten, Dropout, Activation
from keras.layers import Conv1D, MaxPooling1D, AveragePooling1D
from keras.models import Model
from keras.callbacks import ModelCheckpoint
from sklearn.metrics import confusion_matrix
#Flask stuff
from flask import Flask, request, jsonify
from flask_cors import CORS

#Import packages for CNN
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, Conv1D 
from tensorflow.keras.layers import Dense, Dropout, Embedding, LSTM, BatchNormalization, Flatten, MaxPooling2D
from sklearn.model_selection import train_test_split

#Label Encoding
from keras.utils import np_utils
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import StandardScaler

app = Flask(__name__)
CORS(app)

def fitData(df):
    #Split features from targets
    X = df.iloc[:,:-1]
    #Split targets
    y = df.iloc[:,-1]
    #Split train & test dataset
    X_train, X_test, y_train, y_test = train_test_split(X, y, 
                                                        test_size=0.2,
                                                        stratify = y,
                                                        random_state=424)
    #Normalize the data
    scaler = StandardScaler()
    scaler.fit(X_train)
    return scaler

newdf_ravdess= pd.read_csv('C:/www/MoodMagik/Flask stuff/5Emotion.csv', index_col=0)
scaler = fitData(newdf_ravdess)
model = keras.models.load_model('C:/www/MoodMagik/Flask stuff/83acc.h5')




@app.route("/getResult", methods=['POST'])
def getResult():
    if True:
        try:
            file = request.files['file']
            # do the actual work
            result = recogEmotion(file,model,scaler)
            print('\n------------------------')
            print('\nresult: ', result)
            return result
        except Exception as e:
            return jsonify({
                "code": 500,
                "exception":e,
                "message": " Internal error, try again later" 
            })
    # if reached here, not a JSON request.
    return jsonify({
        "code": 400,
        "message": "Invalid JSON input: " + str(request.get_data())
    }), 400


def recogEmotion(audioFile, model, scaler):
    try:
        X, sr = librosa.load(audioFile, sr=22050, duration=4.0)
        mfccs = librosa.feature.mfcc(y=X, sr=sr, n_mfcc=40)
        mfccs = np.mean(mfccs, axis=1)

        mfccs_scaled = scaler.transform(mfccs.reshape(1, -1))
        mfccs_scaled = np.expand_dims(mfccs_scaled, axis=2)

        pred = model.predict(mfccs_scaled)
        pred_class = np.argmax(pred)
        conf = pred[0][pred_class]
        label_map = {0: 'angry', 1: 'fear', 2: 'happy', 3: 'neutral', 4: 'sad'}
        result = label_map[pred_class]

        toReturn = {}
        for i in range(len(pred[0])):
            toReturn[str(label_map[i])] = float(pred[0][i])
        return jsonify(toReturn)

    except:
        return jsonify({
                            "code": 500,
                            "message": 'Internal error, unable to return result'
                        })


if __name__ == "__main__":
    print("This is flask for optimizing route...")
    app.run(debug=True)
    