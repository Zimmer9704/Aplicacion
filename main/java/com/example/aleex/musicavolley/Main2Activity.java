package com.example.aleex.musicavolley;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class Main2Activity extends AppCompatActivity {

    Button guar, can, cons;
    ImageView foto;
    Spinner album;
    EditText nom,art;

    Uri FilePath, path;
    Bitmap bitmap;

    int a=0;
    private static final int PICK_IMAGE = 100;
    private final int PICKER=1;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    ArrayAdapter<String> adaptador1;
    private JSONArray users;
    String imagen = "";
    String audio = "";
    private String url;
    private static String ruta,ruta2;
    MediaPlayer mp = new MediaPlayer();
    JSONArray vectorJSON;
    public List<String> lista1 = Arrays.asList("Rock", "Electronica", "Pop", "Metal", "Romantica");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        guar=(Button)findViewById(R.id.guardar);
        can=(Button)findViewById(R.id.musica);
        cons=(Button)findViewById(R.id.buscar);

        foto=(ImageView)findViewById(R.id.imagen);

        album=(Spinner)findViewById(R.id.album);
        adaptador1=new ArrayAdapter<String>(Main2Activity.this,android.R.layout.simple_list_item_1,lista1);
        album.setAdapter(adaptador1);

        nom=(EditText)findViewById(R.id.edtTitulo);
        art=(EditText)findViewById(R.id.edtArtista);

        guar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.42:88/Android/Musica/insertar.php";
                guadarInfo();

            }
        });

        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                mp.start();
                a=2;
                try{
                    startActivityForResult(
                            Intent.createChooser(intent,"Administrador de archivos"),PICKER);
                }catch (android.content.ActivityNotFoundException ex){}
            }
        });

        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.42:88/Android/Musica/consultar.php";
                sendAndRequestResponse(0);
            }
        });

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicacion "),10);
                a=1;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.stop();
    }

    private void guadarInfo(){
        mRequestQueue = Volley.newRequestQueue(Main2Activity.this);

        mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                map.put("nombre", nom.getText().toString());
                map.put("artista", art.getText().toString());
                map.put("ima",ruta.toString());
                map.put("cancion",audio.toString());
                map.put("album",album.getSelectedItem().toString());

                return map;
            }
        };
        mRequestQueue.add(mStringRequest);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(a==1) {
            if (resultCode == RESULT_OK) {

                Cursor c = getContentResolver().query(data.getData(), null, null, null, null);
                c.moveToFirst();

                ruta = c.getString(1);
                c.close();
                bitmap = BitmapFactory.decodeFile(ruta);
                foto.setImageBitmap(bitmap);


            }
        }
        if(a==2){
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            cursor.moveToFirst();
            audio = cursor.getString(1);
            cursor.close();
            try {
                if (mp.isPlaying())
                {
                    mp.reset();
                }
                mp.setDataSource(audio);
                mp.prepare();
               // mp.start();
                mp.seekTo(0);

            } catch (Exception e) {
                Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
            };
        }

    }

    private void sendAndRequestResponse(final int indice){
        mRequestQueue = Volley.newRequestQueue(Main2Activity.this);

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
                map.put("param1", album.getSelectedItem().toString());
               // map.put("param2", nom.getText().toString());
                return map;
            }
        };
        mRequestQueue.add(mStringRequest);

    }
    private void llenar(final int indice){
        try{

            nom.setText(vectorJSON.getJSONObject(indice).getString("nombre").toString());
            art.setText(vectorJSON.getJSONObject(indice).getString("artista").toString());
            mostrar(vectorJSON.getJSONObject(indice).getString("ima").toString());
            mostrar2(vectorJSON.getJSONObject(indice).getString("cancion").toString());




        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    private void mostrar(String d){
        bitmap = BitmapFactory.decodeFile(d.toString());
        foto.setImageBitmap(bitmap);

    }
    private void mostrar2(String d){
        try {
            if (mp.isPlaying())
            {
                mp.reset();
            }
            mp.setDataSource(d);
            mp.prepare();
            mp.seekTo(0);
            mp.start();
        } catch (Exception e) {
            Toast.makeText(getApplication(), "error exception", Toast.LENGTH_SHORT).show();
        };


    }
}