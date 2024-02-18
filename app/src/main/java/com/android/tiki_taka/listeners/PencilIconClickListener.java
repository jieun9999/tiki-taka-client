package com.android.tiki_taka.listeners;

import android.net.Uri;

import java.util.ArrayList;

public interface PencilIconClickListener {
    void pencilIconClicked(ArrayList<Uri> uriList, int position);
}
