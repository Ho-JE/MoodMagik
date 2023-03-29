import pydub

mp3_file = pydub.AudioSegment.from_file("F:/Code/MoodMagik/Flask stuff/EmotionRecording_20230329_0354_23.mp3", format="mp3")
mp3_file.export("temp.wav", format="wav")