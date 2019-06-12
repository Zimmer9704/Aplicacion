package com.example.aleex.musicavolley;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button atras, adelante, play, favorito;
    TextView tmp1, tmp2, nombre;
    SeekBar tempo;
    ImageView img;
    Spinner combo;
    boolean switchstate;
    Switch funcion;

    MediaPlayer mp = new MediaPlayer();
    JSONArray vectorJSON;
    Bitmap bitmap;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    int contador = 0;
    int con =0;
    String favo ="no";
    int a=0;
    String z="0";
    String id;
    private String url;
    private String url2="http://192.168.43.42:88/Android/Musica/favorito.php";
    private String url3="http://192.168.43.42:88/Android/Musica/actualizar.php";
    public List<String> lista2 = Arrays.asList("Rock", "Electronica", "Pop", "Metal", "Romantica","Favoritos");
    ArrayAdapter<String> adaptador1;
    String audio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        atras=(Button)findViewById(R.id.button1);
        play=(Button)findViewById(R.id.button2);
        adelante=(Button)findViewById(R.id.button3);
        favorito=(Button)findViewById(R.id.button4);
        SensorManager mSensorManager;

        tmp1=(TextView)findViewById(R.id.textView2);
        tmp2=(TextView)findViewById(R.id.textView4);
        nombre=(TextView)findViewById(R.id.textView3);

        combo = (Spinner) findViewById(R.id.spinner);
        adaptador1=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,lista2);
        combo.setAdapter(adaptador1);

        tempo=(SeekBar)findViewById(R.id.seekBar);

        img=(ImageView)findViewById(R.id.imageView);

        funcion=(Switch)findViewById(R.id.switch1);

        mSensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),SensorManager.SENSOR_DELAY_NORMAL);




        atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.42:88/Android/Musica/combo.php";

                if (favo.equals("no"))
                {
                    if(contador==0)
                    {
                        contador=vectorJSON.length();
                        sendAndRequestResponse(contador);

                    }
                    else
                    {
                        contador=contador-1;
                        sendAndRequestResponse(contador);
                    }
                }
                if(favo.equals("si"))
                {
                    if(contador==0)
                    {
                        contador=vectorJSON.length();
                        sendAndRequestResponse2(contador);

                    }
                    else
                    {
                        contador=contador-1;
                        sendAndRequestResponse2(contador);
                    }
                }
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a==0){
                    play.setBackgroundResource(R.mipmap.play);
                    mp.pause();
                    con= mp.getCurrentPosition();
                    a=1;
                }
                else{
                    play.setBackgroundResource(R.mipmap.pause);
                    mp.seekTo(con);
                    mp.start();
                    a=0;
                }
            }
        });

        adelante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.42:88/Android/Musica/combo.php";
                contador=contador+1;
                if (favo.equals("no"))
                {
                    if(contador==vectorJSON.length())
                    {
                        contador=0;
                        sendAndRequestResponse(contador);
                    }
                    else
                    {
                        sendAndRequestResponse(contador);
                    }
                }
                if(favo.equals("si"))
                {
                    if(contador==vectorJSON.length())
                    {
                        contador=0;
                        sendAndRequestResponse2(contador);
                    }
                    else
                    {
                        sendAndRequestResponse2(contador);
                    }
                }
            }
        });

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(z.equals("0")){
                    favorito.setBackgroundResource(R.mipmap.heartblack);
                    z="1";
                    guadarInfo();
                }
                else{
                    favorito.setBackgroundResource(R.mipmap.heart);
                    z="0";
                    guadarInfo();
                }
            }
        });

        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((combo.getSelectedItem().toString().equals("Favoritos"))){
                    favo="si";
                    sendAndRequestResponse2(0);
                    //new AsyncTask_load().execute();
                    contador=0;
                }
                else{
                    url = "http://192.168.43.42:88/Android/Musica/combo.php";
                    favo="no";
                    sendAndRequestResponse(0);
                    //new AsyncTask_load().execute();
                    contador=0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tempo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mp.seekTo(tempo.getProgress());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
    }
    private String minutos(int milliseconds )
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        return ((hours<10)?"0"+hours:hours) + ":" +
                ((minutes<10)?"0"+minutes:minutes) + ":" +
                ((seconds<10)?"0"+seconds:seconds);
    }
    private void sendAndRequestResponse(final int indice){
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("nombre");
                    llenar(indice);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                HashMap<String, String> map= new HashMap<>();
                map.put("param1", combo.getSelectedItem().toString());
                //map.put("param2", edt1.getText().toString());
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);

    }
    private void sendAndRequestResponse2(final int indice){
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject = new JSONObject(response);
                    vectorJSON = jsonObject.getJSONArray("nombre");
                    llenar(indice);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
            }
        });
        mRequestQueue.add(mStringRequest);

    }
    private void guadarInfo(){
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url3, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG","Error: "+error.toString());
            }
        }){
            @Override
            protected HashMap<String,String> getParams(){
                HashMap<String, String> map= new HashMap<>();
                map.put("ID", id);
                map.put("Favoritos",z);

                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
    private void llenar(final int indice){
        try{

            nombre.setText(vectorJSON.getJSONObject(indice).getString("Nombre").toString());
            mostrar(vectorJSON.getJSONObject(indice).getString("Imagen").toString());
            mostrar2(vectorJSON.getJSONObject(indice).getString("Cancion").toString());
            id=vectorJSON.getJSONObject(indice).getString("ID").toString();
            z=vectorJSON.getJSONObject(indice).getString("Favoritos").toString();


            if(z.equals("1")){
                favorito.setBackgroundResource(R.mipmap.heartblack);
            }
            if(z.equals("0")){
                favorito.setBackgroundResource(R.mipmap.heart);
            }




        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void mostrar(String d){
        bitmap = BitmapFactory.decodeFile(d.toString());
        img.setImageBitmap(bitmap);

    }
    private void mostrar2(String audio){
        try {
            if (mp.isPlaying())
            {
                mp.reset();
            }
            mp.setDataSource(audio);
            mp.prepare();
            mp.seekTo(0);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
        };
        //play.setBackgroundResource(R.mipmap.stop);
        new AsyncTask_load().execute();



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this){
            switchstate= funcion.isChecked();
            if(switchstate==true) {

                switch (event.sensor.getType()){

                    case Sensor.TYPE_PROXIMITY:
                        if (event.values[0] != 0){
                        }else {
                            if(mp.isPlaying()){
                                play.setBackgroundResource(R.mipmap.pause);
                                mp.pause();
                                con=mp.getCurrentPosition();
                                a=1;
                            }else{
                                play.setBackgroundResource(R.mipmap.play);
                                mp.seekTo(con);
                                mp.start();
                                a=0;
                            }
                        }
                        break;

                    case Sensor.TYPE_GYROSCOPE:
                        if(event.values[2]> 3 ) {
                            url = "http://192.168.43.42:88/Android/Musica/combo.php";
                            contador = contador + 1;
                            if (favo.equals("no"))
                            {
                                if (contador==vectorJSON.length()){
                                    contador=0;
                                    sendAndRequestResponse(contador);
                                }
                                else{
                                    sendAndRequestResponse(contador);
                                }
                            }
                            if(favo.equals("si"))
                            {
                                if (contador==vectorJSON.length()){
                                    contador=0;
                                    sendAndRequestResponse2(contador);
                                }
                                else{
                                    sendAndRequestResponse2(contador);
                                }
                            }

                        }
                        if (event.values[0]>=3){
                            url= "http://192.168.43.42:88/Android/Musica/combo.php";
                            if(favo.equals("no")){
                                if (contador==0){
                                    contador=vectorJSON.length();
                                    sendAndRequestResponse(contador);
                                }
                                else {
                                    contador=contador-1;
                                    sendAndRequestResponse(contador);
                                }
                            }
                        }if(favo.equals("si"))
                    {
                        if (contador==0){
                            contador=vectorJSON.length();
                            sendAndRequestResponse2(contador);
                        }
                    }

                    break;
                    case Sensor.TYPE_LIGHT:
                        float vol=(event.values[0])/100;
                        if(vol>=1){
                            vol=1;
                        }
                        mp.setVolume(vol,vol);
                        break;
                }

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class AsyncTask_load extends AsyncTask<Void, Integer, Void> {
        int x=0;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tempo.setMax(mp.getDuration());
            x=tempo.getMax();
            tmp2.setText(minutos(mp.getDuration()));
        }
        @Override
        protected Void doInBackground(Void... params) {

            while(tempo.getProgress()<=x){
                publishProgress(mp.getCurrentPosition());
                SystemClock.sleep(1000);
            }

            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            tempo.setProgress(values[0]);
            tmp1.setText(minutos(values[0]));
        }
        @Override
        protected void onPostExecute(Void result) {
            //Toast.makeText(MainActivity.this, "Progreso terminado", Toast.LENGTH_LONG).show();
            //btnProgress.setClickable(true);
        }
    }
}