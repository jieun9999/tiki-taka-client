package com.android.tiki_taka.models.response;

public class FolderDeletedResponse {
    private boolean success;
    private  boolean folderDeleted;

    public FolderDeletedResponse(boolean success, boolean folderDeleted) {
        this.success = success;
        this.folderDeleted = folderDeleted;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFolderDeleted() {
        return folderDeleted;
    }

    public void setFolderDeleted(boolean folderDeleted) {
        this.folderDeleted = folderDeleted;
    }
}
