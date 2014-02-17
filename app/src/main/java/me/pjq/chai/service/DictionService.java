package me.pjq.chai.service;

import android.text.TextUtils;
import me.pjq.chai.EFLogger;
import me.pjq.chai.MyApplication;
import me.pjq.chai.ServiceProvider;
import me.pjq.httpclient.WordListUpdater;

import java.io.*;
import java.util.HashMap;

/**
 * Created by pengjianqing on 2/14/14.
 */
public class DictionService {
    private static final String TAG = DictionService.class.getSimpleName();

    private static final String WORDS_PATH = "word.list";

    private static DictionService instance;

    HashMap<String, String> dictions;

    public DictionService() {
        if (null == dictions) {
            parser();
        }
    }

    public void updateFromServer() {
        WordListUpdater wordListUpdater = new WordListUpdater();
        wordListUpdater.updateFromServer();
    }


    public static DictionService getInstance() {
        if (null == instance) {
            instance = new DictionService();
        }

        return instance;
    }

    public HashMap<String, String> getDictions() {
        return dictions;
    }

    public String getValue(String value) {
        String result = value;

        if (null != dictions && dictions.containsKey(value)) {
            result = dictions.get(value);
            result = result.replace(" ", "");
        }

        return result;
    }

    public String convert(String input) {
        if (TextUtils.isEmpty(input)) {
            return input;
        }

        char[] inputs = input.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char ch : inputs) {
            String newCh = getValue(String.valueOf(ch));
            stringBuilder.append(newCh);
        }

        return stringBuilder.toString();
    }


    public void parser() {
        ServiceProvider.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                HashMap<String, String> hashMap = null;
                String path = WORDS_PATH;
                try {
                    InputStream inputStream = null;
                    if (WordListUpdater.isWordListCached()) {
                        String cachePath = WordListUpdater.getWordlistCachePath();
                        EFLogger.i(TAG, cachePath + " is already cached locally.");
                        inputStream = new FileInputStream(new File(cachePath));
                    } else {
                        inputStream = MyApplication.getContext().getAssets().open(path);
                    }

                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader rd = new BufferedReader(isr);
                    String line = "";

                    hashMap = new HashMap<String, String>();
                    while ((line = rd.readLine()) != null) {
                        if (line.endsWith(";")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        String[] values = line.split(" ");
                        if (null == values || values.length < 2) {
                            continue;
                        }
                        String key = values[0];
                        String value = line.substring(1);

                        hashMap.put(key, value);
                    }

                    isr.close();
                    rd.close();
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long end = System.currentTimeMillis();
                EFLogger.i(TAG, "init use time " + (end - start));

                dictions = hashMap;
            }
        });
    }
}
