package com.huy.pdoc.service;

import org.springframework.stereotype.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;

@Service
public class FirebaseService {
    public void delete(String filePath) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filePath);
        if (blob != null) {
            blob.delete();
            System.out.println("File deleted successfully.");
        } else {
            System.out.println("File not found.");
        }
    }
}
