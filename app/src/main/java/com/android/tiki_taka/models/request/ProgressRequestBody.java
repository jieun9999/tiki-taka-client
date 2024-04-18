package com.android.tiki_taka.models.request;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ProgressRequestBody extends RequestBody {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final File file;
    private final UploadCallbacks listener;
    private final String contentType;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
    }

    public ProgressRequestBody(File file, String contentType, UploadCallbacks listener) {
        this.file = file;
        this.listener = listener;
        this.contentType = contentType;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return MediaType.parse(contentType);
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        try (Source source = Okio.source(file)) {
            Buffer buffer = new Buffer();
            Long fileSize = contentLength();
            long uploaded = 0;
            long read;

            while ((read = source.read(buffer, DEFAULT_BUFFER_SIZE)) != -1) {
                sink.write(buffer, read);
                uploaded += read;
                listener.onProgressUpdate((int) (uploaded * 100 / fileSize));
            }
        }
    }
}
