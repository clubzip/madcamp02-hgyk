# Description

### This package contains three tabs, that is, Gallery tab, Contact tab, and Calendar tab.

## 1. Gallery tab
The first tab is a gallery of images of the app's own external storage. Click on the image in the gallery to see the enlarged picture. In this state, you can view the next image in slide motion. You can view the selection mode by holding down the image in the gallery for a while. Select the image in Select mode and press the camera-shaped floating button to delete it. (to be changed to a proper icon in the future) Pressing the camera-shaped floating button in the default mode instead of the select mode becomes the filming mode. Photos taken at this time are immediately reflected in the gallery. Photos taken in this app are not affected by other apps.



## 2. Contact tab
The Contact tab prints the list of contact data which is stored in JSON file at its internal storage.
User can add the contact information containing profile image, name, and mobile number by touching '+' button.
The profile image is selected from device's own gallery.
In each contact item activity, user can execute the dial and sms to the data. Additionally, user can remove the data from JSON file.
The Edit button is not implemented yet.

This fragment contains custom listview. Thus, it contains 'ListViewAdapter' which extends BaseAdapter and 'ListViewItem'.
ListViewAdapter implements Filterable for searching function. Each ListViewItem has three private member variables that contain the information of name, mobile number, and the name of profile image file. ListViewAdapter creates view for each row of custom listview using this data. Initially, these data are from JSON file at internal storage.

The addition and remove of contact data is implemented by rewrite the JSON file. To prints the changed listview, the activities for addition and remove are start by calling startActivityForResult. And the Fragment renew itself in the method 'onActivityResult.'

The calling and Sending message is implemented with Uri.parse method.



## 3. Work time checker



