package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class Image {
    private String mAbsolutePath;
    private int mHieght;
    private int mWidth;

    private String mImageDirPath;
    private String mImageName;

    private Bitmap mScaledImage;
    private Bitmap mOriginalImage = null;
    private Bitmap mExerciseImage = null;

    public Image(String path, int cellSize) {
        mAbsolutePath = path;
        mHieght = cellSize;
        mWidth = cellSize;

        mImageDirPath = getImageDirPath();
        mImageName = getImageName();
    }

    public Image(String path, int height, int width) {
        mAbsolutePath = path;
        mHieght = height;
        mWidth = width;
    }

    public Bitmap getOriginalImage() {
        if (mOriginalImage == null)
            try {
                mOriginalImage = rotateImage(BitmapFactory.decodeFile(mAbsolutePath, new BitmapFactory.Options()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return mOriginalImage;
    }

    public Bitmap getScaledImage() {
        if (mScaledImage == null)
            try {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                /* Get the dimensions of the bitmap */
                BitmapFactory.decodeFile(mAbsolutePath, bmOptions);
                int imageHeight = bmOptions.outHeight;
                int imageWidth = bmOptions.outWidth;

                /* Get the SCALE_FACTOR that is a power of 2 and
                    keeps both height and width larger than CELL_SIZE. */
                int scaleFactor = 1;
                if (imageHeight > mHieght || imageWidth > mWidth) {
                    final int halfHeight = imageHeight / 2;
                    final int halfWidth = imageWidth / 2;

                    while ((halfHeight / scaleFactor) >= mHieght
                            && (halfWidth / scaleFactor) >= mWidth) {
                        scaleFactor *= 2;
                    }
                }

                /* Decode the image file into a Bitmap sized to fill the View */
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                mScaledImage = rotateImage(BitmapFactory.decodeFile(mAbsolutePath, bmOptions));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return mScaledImage;
    }

    public void saveExerciseImage (String startTimeId) {
        File dir = new File(mImageDirPath);
        if(!dir.exists())
            dir.mkdirs();

        try {
            File image = new File(mImageDirPath + "/Exercise/" + startTimeId + mImageName);
            if (!image.exists()) {
                image.createNewFile();
            }
            OutputStream out = new FileOutputStream(image);

            getScaledImage().compress(Bitmap.CompressFormat.JPEG, 100, out);
            mExerciseImage = mScaledImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAbsolutePath() {
        return mAbsolutePath;
    }

    private Bitmap rotateImage (Bitmap bitmap) throws IOException {
        /* Code below rotates the bitmap to be original direction */
        ExifInterface exif = new ExifInterface(mAbsolutePath);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                break;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String getImageDirPath() {
        return mAbsolutePath.substring(0, mAbsolutePath.lastIndexOf('/'));
    }

    private String getImageName() {
        return mAbsolutePath.substring(mAbsolutePath.lastIndexOf('/') + 1);
    }
}
