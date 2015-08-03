package nilson.sdcard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button btIniciar;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView)findViewById(R.id.tvResult);

        btIniciar=(Button)findViewById(R.id.btIniciar);
        btIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizapasta();
            }
        });

    }

    //work with the directories
    private void atualizapasta(){
        Intent seguir = new Intent(MainActivity.this, ListaPasta.class);
        seguir.putExtra("action","BKP");
        startActivityForResult(seguir, 1);
    }

    //use the directoy returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case (1):{
                if(resultCode==Activity.RESULT_OK){
                    tvResult.setText(data.getStringExtra("retorno"));
                    btIniciar.setText("Choose another folder");
                }
                break;
            }
        }
    }
}