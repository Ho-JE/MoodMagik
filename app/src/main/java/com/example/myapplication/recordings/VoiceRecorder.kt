package com.example.myapplication.recordings

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream

class VoiceRecorder {

    private lateinit var recorder: AudioRecord
    private lateinit var buffer: ByteArray
    private val record: ByteArrayOutputStream = ByteArrayOutputStream()
    private var partialRecord: ByteArrayOutputStream = ByteArrayOutputStream()
    private var start = false

    @SuppressLint("MissingPermission")
    fun prepare(sampleRate: Int, frameSize: Int) : VoiceRecorder {
        val minBufferSize =
            AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            minBufferSize
        )
        buffer = ByteArray(frameSize)
        recorder.startRecording()
        return this
    }

    fun start() {
        if (!start) {
            start = true
            Thread {
                while (start) {
                    recorder.read(buffer, 0, buffer.size)
                    record.write(buffer)
                    partialRecord.write(buffer)
                }
            }.start()
        }
    }

    fun stop() : ByteArray {
        start = false
        recorder.stop()
        val result = record.toByteArray()
        record.reset()
        return result
    }
    fun stopShort(): ByteArray {
        val returning = partialRecord.toByteArray()
        partialRecord = ByteArrayOutputStream()
        return returning
    }

    fun release() {
        start = false
        record.reset()
        recorder.release()
    }
}