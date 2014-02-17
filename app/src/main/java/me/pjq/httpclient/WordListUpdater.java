package me.pjq.httpclient;

import java.io.*;
import java.net.MalformedURLException;

import me.pjq.chai.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;

public class WordListUpdater {
    private static final String TAG = WordListUpdater.class.getSimpleName();
    public static final String WORDLIST_CACHE_PATH = "cache/word.list";
    private Context mContext;
    private InputStream mServerInputStream;

    public WordListUpdater() {
        EFLogger.i(TAG, "WordListUpdater()");
        mContext = MyApplication.getContext();
    }

    public void updateFromServer() {
        if (ApplicationConfig.INSTANCE.UPDATE_WORDLIST_FROM_SERVER()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Need support https
                        HttpClient httpClient = HttpClientHelper.getInstance()
                                .createNewDefaultHttpClient();
                        HttpGet httpGet = new HttpGet(mContext.getString(R.string.wordlist_url));
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        mServerInputStream = httpResponse.getEntity()
                                .getContent();

                        if (null != mServerInputStream) {
                            String result = HttpClientHelper.readFromInputStream(new InputStreamReader(mServerInputStream));
                            String path = getWordlistCachePath();

                            if (isWordListCached()) {
                                new File(path).delete();
                            }

                            File file = new File(path);

                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }

                            file.createNewFile();

                            EFLogger.i(TAG, "WordListUpdater(), save file " + path);
                            writeFile(path, result);

                            mServerInputStream.close();
                            mServerInputStream = null;
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static String getWordlistCachePath() {
        String path = LocalPathResolver.getInternalDataDataPath(MyApplication.getContext()) + File.separator + WORDLIST_CACHE_PATH;
        return path;
    }

    public static boolean isWordListCached() {
        String path = getWordlistCachePath();

        return new File(path).exists();
    }


    /**
     * save data to local file
     *
     * @param data
     */
    private void writeFile(String fileName, String data) {
        BufferedWriter buf = null;
        try {
            buf = new BufferedWriter(new FileWriter(fileName, true));
            buf.write(data, 0, data.length());
            buf.newLine();

            if (buf != null) {
                buf.close();
                buf = null;
            }
        } catch (OutOfMemoryError oom) {
            oom.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buf != null) {
                    buf.close();
                    buf = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
