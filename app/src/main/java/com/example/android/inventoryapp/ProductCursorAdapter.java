package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.ProductContract.ProductEntry;

import static com.example.android.inventoryapp.data.ProductProvider.LOG_TAG;

/**
 * {@link ProductCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of product data as its data source. This adapter knows
 * how to create list items for each row of product data in the {@link Cursor}.
 */
public class ProductCursorAdapter extends CursorAdapter {

    private Button saleButton;
    private int quantity;

    /**
     * Constructs a new {@link ProductCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);

        // If the quantity is empty string or null, then use some default text
        // that says "Unknown info", so the TextView isn't blank.
        saleButton = view.findViewById(R.id.sale_button);
        final int productId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        final String finalProductQuantity = productQuantity;
        saleButton.setOnClickListener(new View.OnClickListener() {
            //    @Override
            public void onClick(View view) {
                //getting the int value of the String productQuantity
                quantity = Integer.parseInt(finalProductQuantity);
                Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);
                saleButton(context, productUri, quantity);
            }
        });
    }

    private void saleButton(Context context, Uri uri, int quantity) {

        if (quantity == 0) {
            Log.e(LOG_TAG, context.getString(R.string.empty_stock));
        } else {
            quantity--;

            ContentValues contentValues = new ContentValues();
            contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
            int rowsAffected = context.getContentResolver().update(uri, contentValues, null, null);

            // Display error message in Log if sale button fails to update quantity
            if (!(rowsAffected > 0)) {
                Log.e(LOG_TAG, context.getString(R.string.error_sale_update));
            }
        }
    }
}