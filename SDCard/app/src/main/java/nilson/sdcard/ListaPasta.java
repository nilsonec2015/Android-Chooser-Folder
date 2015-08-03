package nilson.sdcard;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListaPasta extends ListActivity {

    //stores the names and the directories from a device
    private ArrayList<String>[] sds;

    private ArrayAdapter<String> adapda;
    String path="", action="";
    //level of the navigation folder
    int nivel=0;

    Button btEscolhe,btVoltar;
    TextView tvAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //the action that this class will
        action = getIntent().getStringExtra("action");

        sds=getStoragepath();
        adapda = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, sds[0]);
        setListAdapter(adapda);

        setContentView(R.layout.lista);
        tvAtual = (TextView)findViewById(R.id.tvAtual);
        btEscolhe=(Button)findViewById(R.id.btEscolhe);
        btEscolhe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("BKP".equals(action)){
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Directory selected to "+action+": "+tvAtual.getText(), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    Intent resultado = new Intent();
                    resultado.putExtra("retorno", tvAtual.getText().toString());
                    if(getParent()==null){
                        setResult(Activity.RESULT_OK, resultado);
                    }else{
                        getParent().setResult(Activity.RESULT_OK, resultado);
                    }
                    finish();
                }else if("export_report".equals(action)){

                }else if("orther_action".equals(action)){

                }
            }
        });

        btVoltar = (Button)findViewById(R.id.btVoltar);
        btVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if (!btVoltar.isEnabled()) {
            this.finish();
            return;
        }

        if(nivel>1) {
            sds = getSelectpath(path);//lista diretorios de uma pasta em especifico
            tvAtual.setText(path);
        }else {
            sds = getStoragepath();//Lista os discos de armazenamento (SD,USB,etc.)
            nivel=1;
            btEscolhe.setEnabled(false);
            btVoltar.setEnabled(false);
            tvAtual.setText("/");
        }
        nivel--;

        adapda = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, sds[0]);
        setListAdapter(adapda);
        path=path.substring(0,path.lastIndexOf("/"));
    }

    protected  void onListItemClick(ListView listView, View view, int posicao, long id){
        super.onListItemClick(listView, view, posicao, id);

        path=sds[1].get(posicao);
        tvAtual.setText(path);
        path=path.substring(0, path.lastIndexOf("/"));

        sds=getSelectpath(sds[1].get(posicao));
        ArrayAdapter<String> adapda = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sds[0]);
        setListAdapter(adapda);

        btEscolhe.setEnabled(true);
        btVoltar.setEnabled(true);
        nivel++;
    }

    private ArrayList<String>[] getSelectpath(String path) {
        //list with the paths of the directories
        ArrayList<String> fileList = new ArrayList<>();
        //list with the names of the directories
        ArrayList<String> labels = new ArrayList<>();
        File dir = new File(path);
        //finds and stores the paths and the names of subdirectories
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (File aListFile : listFile) {
                if (aListFile.isDirectory()) {
                    fileList.add(aListFile.getPath());
                    labels.add(aListFile.getName());
                }
            }
        }
        ArrayList<String>[] result = new ArrayList[2];
        result[0]=labels;
        result[1]=fileList;
        return result;
    }

    public ArrayList<String>[] getStoragepath() {
        //list with the paths of the storages
        ArrayList<String> fileList = new ArrayList<>();
        //list with the names of the storages
        ArrayList<String> labels = new ArrayList<>();
        //the internal storage of the device
        String sdInterno=System.getenv("EXTERNAL_STORAGE");
        fileList.add(sdInterno);
        labels.add(sdInterno.substring(sdInterno.lastIndexOf("/") + 1));

        //the external storages connected in moment
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;

                if (line.contains("fat")) {// TF card
                    String columns[] = line.split(" ");
                    String diretorio=line;
                    if (columns != null && columns.length > 2) {
                        for (String column : columns) {
                            if (column.contains("storage")) {
                                diretorio = column;
                                break;
                            }
                        }
                        // check directory is exist or not
                        File dir = new File(diretorio);
                        if (dir.exists() && dir.isDirectory()) {
                            //work with the storage here
                            fileList.add(diretorio);
                            labels.add(diretorio.substring(diretorio.lastIndexOf("/")+1));
                        }
                    }
                }
            }


        } catch (Exception ignored) {

        }
        ArrayList<String>[] result = new ArrayList[2];
        result[0]=labels;
        result[1]=fileList;
        return result;
    }

}