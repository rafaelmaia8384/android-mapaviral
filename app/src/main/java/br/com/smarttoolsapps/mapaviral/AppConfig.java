package br.com.smarttoolsapps.mapaviral;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;

public class AppConfig {

    private Storage storage;
    private Context context;
    private Gson gson = new Gson();

    private static final String CONFIG_MSG_INTRO = "CONFIG_MSG_INTRO.data";
    private static final String CONFIG_USUARIO = "CONFIG_USUARIO.data";
    private static final String CONFIG_SISTEMA = "CONFIG_SISTEMA.data";
    private static final String CONFIG_HEATMAP = "CONFIG_HEATMAP.data";
    private static final String CONFIG_ALERTAS = "CONFIG_ALERTAS.data";
    private static final String CONFIG_AVISO = "CONFIG_AVISO.data";

    private static final int MIN_ALERTA_TIME = 24 * 60 * 60 * 1000; // 24h

    public AppConfig(Context context) {

        this.context = context;

        storage = new Storage(context);
    }

    public boolean alertaExistente(String id_aparelho) {

        if (storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + id_aparelho + ".alerta")) {

            File file = new File(storage.getInternalFilesDirectory() + File.separator + id_aparelho + ".alerta");

            if ((System.currentTimeMillis() - file.lastModified()) / 1000L > MIN_ALERTA_TIME) {

                storage.deleteFile(file.getAbsolutePath());

                return false;
            }
            else {

                return true;
            }
        }
        else {

            return false;
        }
    }

    public void registrarAlerta(String id_aparelho) {

        storage.createFile(getConfigFile(id_aparelho) + ".alerta", "alerta");
    }

    public boolean isHideAvisoInicial() {

        return storage.isFileExist(getConfigFile(CONFIG_AVISO));
    }

    public void setHideAvisoInicial(boolean hide) {

        if (hide) storage.createFile(getConfigFile(CONFIG_AVISO), "hide");
        else storage.deleteFile(getConfigFile(CONFIG_AVISO));
    }

    public boolean isHideConfigMsgIntro() {

        return storage.isFileExist(getConfigFile(CONFIG_MSG_INTRO));
    }

    public void setHideConfigMsgIntro(boolean hide) {

        if (hide) storage.createFile(getConfigFile(CONFIG_MSG_INTRO), "hide");
        else storage.deleteFile(getConfigFile(CONFIG_MSG_INTRO));
    }

    private String getConfigFile(String fileName) {

        return storage.getInternalFilesDirectory() + File.separator + fileName;
    }

    public void setUsuario(String json) {

        storage.createFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_USUARIO, json);
    }

    public ModelUsuario getUsuario() {

        return gson.fromJson(storage.readTextFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_USUARIO), ModelUsuario.class);
    }

    public void setSistema(String json) {

        storage.createFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_SISTEMA, json);
    }

    public ModelSistema getSistema() {

        return gson.fromJson(storage.readTextFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_SISTEMA), ModelSistema.class);
    }

    public void setAlertaAtivado(boolean ativar) {

        if (ativar) {

            storage.deleteFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_ALERTAS);
        }
        else {

            storage.createFile(storage.getInternalFilesDirectory() + File.separator + CONFIG_ALERTAS, "ativado");
        }
    }

    public boolean isAlertaAtivado() {

        return !storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + CONFIG_ALERTAS);
    }

    public List<ModelHeatmap> getHeatMap() {

        if (!storage.isFileExist(storage.getInternalFilesDirectory() + File.separator + CONFIG_HEATMAP)) {

            return null;
        }

        File file = new File(storage.getInternalFilesDirectory() + File.separator + CONFIG_HEATMAP);
        List<ModelHeatmap> heatmap = ModelHeatmap.loadFromCsv(file);

        if (heatmap.size() < 30000) {

            if ((System.currentTimeMillis() - file.lastModified()) / 1000L > 60 * 5) {

                return null;
            }
            else {

                return heatmap;
            }
        }
        else if (heatmap.size() < 60000) {

            if ((System.currentTimeMillis() - file.lastModified()) / 1000L > 60 * 10) {

                return null;
            }
            else {

                return heatmap;
            }
        }
        else if (heatmap.size() < 90000) {

            if ((System.currentTimeMillis() - file.lastModified()) / 1000L > 60 * 15) {

                return null;
            }
            else {

                return heatmap;
            }
        }
        else {

            if ((System.currentTimeMillis() - file.lastModified()) / 1000L > 60 * 20) {

                return null;
            }
            else {

                return heatmap;
            }
        }
    }

    public File writeResponseBodyToDisk(ResponseBody body) {

        try {

            File heatMapFile = new File(getConfigFile(CONFIG_HEATMAP));

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(heatMapFile);

                while (true) {

                    int read = inputStream.read(fileReader);

                    if (read == -1) {

                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();

                return heatMapFile;
            }
            catch (IOException e) {

                return null;
            }
            finally {

                if (inputStream != null) {

                    inputStream.close();
                }

                if (outputStream != null) {

                    outputStream.close();
                }
            }
        }
        catch (IOException e) {

            return null;
        }
    }

    public String getDeviceID() {

        return android.os.Build.SERIAL + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String formatarData(String data) {

        SimpleDateFormat entrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat saida = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm:ss");

        try {

            Date dataEntrada = entrada.parse(data);

            return saida.format(dataEntrada);
        }
        catch (Exception e) {

            Log.d("ERROR: ", e.getLocalizedMessage());

            return "00/00/0000 00:00:00";
        }
    }
}
