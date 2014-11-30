package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;
import com.baconworx.smsflash.db.Filterset;
import com.baconworx.smsflash.receivers.MessageReceiver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportPackage extends Activity {
    private static final int FILE_SELECT_CODE = 1;
    private static final int COL_NAME = 0;
    private static final int COL_CAPTION = 1;
    private static final int COL_PATTERN = 2;
    private static final int COL_REPLACEMENT = 3;
    private static final int COL_SOURCENO = 4;
    private static final int COL_COLOR = 5;
    private static final String FILTERLINE_DELIMITER = "<:next:>";

    private Uri currentUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUri = getIntent().getData();
        if (currentUri != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) importFiltersets(currentUri);
                    Toast.makeText(ImportPackage.this,
                            String.format("%s imported successfully!", currentUri.getLastPathSegment()),
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(String.format("Import \"%s\"?", currentUri.getLastPathSegment()))
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        } else showFileChooser();
    }

    private void importFiltersets(Uri uri) {
        Map<String, List<Filter>> filtersets;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            filtersets = getFiltersetsFromFile(reader);
            storeFiltersets(filtersets);
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
    }

    private void storeFiltersets(Map<String, List<Filter>> filtersets) {
        ConfigDatabase configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        for (Map.Entry<String, List<Filter>> entry : filtersets.entrySet()) {
            Filterset filterset = new Filterset(entry.getKey());
            configDatabase.addFilterset(filterset);

            for (Filter filter : entry.getValue()) {
                filter.setFiltersetId(filterset.getId());
                configDatabase.addFilter(filter);
            }
        }

        configDatabase.close();

        MessageReceiver.SetTriggersFromDb(this);
    }

    private Map<String, List<Filter>> getFiltersetsFromFile(BufferedReader reader) throws IOException {
        HashMap<String, List<Filter>> map = new HashMap<String, List<Filter>>();

        String line;
        String currentFilterset = null;
        while ((line = reader.readLine()) != null) {
            if (!line.isEmpty()) {
                // check if filterset specified, e.g. "[name]"
                String filterset = line.replaceAll("^\\[(.*)\\]$", "$1");
                if (!filterset.equals(line)) currentFilterset = filterset;

                    // not filterset this line, parse filter
                else {
                    String[] values = line.split(FILTERLINE_DELIMITER);
                    Filter filter = new Filter();
                    filter.setName(values[COL_NAME]);
                    filter.setCaption(values[COL_CAPTION]);
                    filter.setPattern(values[COL_PATTERN]);
                    filter.setReplacement(values[COL_REPLACEMENT]);
                    filter.setSourceNumber(values[COL_SOURCENO]);
                    filter.setColor(getColorFromString(values[COL_COLOR]));

                    if (currentFilterset != null) {
                        List<Filter> filters = map.get(currentFilterset);
                        if (map.get(currentFilterset) == null) {
                            filters = new ArrayList<Filter>();
                            map.put(currentFilterset, filters);
                        }

                        filters.add(filter);
                    }
                }
            }
        }
        return map;
    }

    private int getColorFromString(String color) {
        String[] rgb = color.split("-");
        return Color.rgb(Integer.parseInt(rgb[0]),
                Integer.parseInt(rgb[1]),
                Integer.parseInt(rgb[2]));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode != RESULT_CANCELED) {
                    Uri uri = data.getData();
                    importFiltersets(uri);
                }
                finish();
                break;
            default:
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra("return-data", true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}