package com.example.pokemon;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private List<Pokemon> pokemonList;
    private List<Pokemon> pokemonGameList;
    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Random random;
    public Integer acertos = 0;
    public Integer erros = 0;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        random = new Random();
        pokemonList = new LinkedList<>();
        pokemonGameList = new ArrayList<>();

        Log.d(TAG, "onCreate: App criada");
        DownloadDeDados downloadDeDados = new DownloadDeDados();
        String aux = "";
        try {
            aux = downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        leFaturasDeJSONString(aux, pokemonList);


        new CountDownTimer(60000, 1000) {

            public void onTick(long millisecondsUntilDone) {

                // Coundown is counting down (every second)

                Log.i("Seconds left", String.valueOf(millisecondsUntilDone / 1000));

            }

            public void onFinish() {
                // Counter is finished! (after 60 seconds)
                Context contexto = getApplicationContext();
                Toast.makeText(contexto, "Acertou: " + acertos.toString() + " Errou: " + erros.toString(), Toast.LENGTH_LONG).show();

                acertos = 0;
                erros = 0;

                DownloadDeDados downloadDeDados = new DownloadDeDados();
                String aux = "";
                try {
                    aux = downloadDeDados.execute("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json").get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                leFaturasDeJSONString(aux, pokemonList);
            }
        }.start();

    }


    public void newGame(){
        pokemonGameList.clear();
        novoPokemon();
        novoPokemon();
        novoPokemon();
        novoPokemon();
        setTextButtons();
        Collections.shuffle(pokemonGameList);
        String imgUrl = pokemonGameList.get(0).getImg();
        downloadImage(imageView, imgUrl);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption(button1.getText().toString().compareTo(pokemonGameList.get(0).getName()));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption(button2.getText().toString().compareTo(pokemonGameList.get(0).getName()));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption(button3.getText().toString().compareTo(pokemonGameList.get(0).getName()));
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOption(button4.getText().toString().compareTo(pokemonGameList.get(0).getName()));
            }
        });
    }

    public void checkOption(int i){
        if(i==0){
            Toast.makeText(this, "Acertou",
                    Toast.LENGTH_LONG).show();
            acertos +=1;
        }
        else{
            Toast.makeText(this, "Errou",
                    Toast.LENGTH_LONG).show();
            erros +=1;
        }
        newGame();
    }

    public void setTextButtons(){
        button1.setText(pokemonGameList.get(0).getName());
        button2.setText(pokemonGameList.get(1).getName());
        button3.setText(pokemonGameList.get(2).getName());
        button4.setText(pokemonGameList.get(3).getName());
    }

    public Pokemon novoPokemon(){
            int indice = random.nextInt(pokemonList.size()+1);
            Pokemon pokemon = pokemonList.get(indice);
            if (pokemonGameList.size()>=1){
                checkPokemon(pokemon);
            }
            insertPokemon(pokemon);



            return pokemon;
    }

    public Pokemon checkPokemon(Pokemon pokemon) {
        for (int i = 0; i <= pokemonGameList.size(); i++) {
            if (pokemon.getName().compareTo(pokemonGameList.get(i).getName().toString())==0) {
                insertPokemon(pokemon);
                pokemonGameList.remove(pokemonGameList.size()-1);
                novoPokemon();
            }
            else {
                return pokemon;
            }
        }
        return null;
    }

    public void insertPokemon(Pokemon pokemon){
        pokemonGameList.add(pokemon);
        Log.d(TAG, "insertPokemon: " + pokemonGameList.size());
    }


    public void downloadImage(ImageView imageView, String Url){
        String TAG = "downloadImage";

        String imgUrl = Url.replace("http", "https");

        ImageDownloader imageDownloader = new ImageDownloader();
        try{
            Bitmap imagem = imageDownloader.execute(imgUrl).get();
            imageView.setImageBitmap(imagem);
        }   catch (Exception e){
            Log.e(TAG, "downloadImage: Erro ao baixar imagem"+e.getMessage());
        }

    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        private static final String TAG = "ImageDownloader";

        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (Exception e){
                Log.e(TAG, "doInBackground: Erro ao baixar imagem" + e.getMessage());
            }

            return null;
        }
    }



    private static List<Pokemon> leFaturasDeJSONString(String jsonString, List pokemonList) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray pokemons = json.getJSONArray("pokemon");

            for (int i = 0; i < pokemons.length(); i++) {
                JSONObject pokemon = pokemons.getJSONObject(i);
                Pokemon p = new Pokemon(
                        pokemon.getInt("id"),
                        pokemon.getString("name"),
                        pokemon.getString("img")
                );
                pokemonList.add(p);
            }
        } catch (JSONException e) {
            System.err.println("Erro fazendo parse de String JSON: " + e.getMessage());
        }

        return pokemonList;
    }

    private class DownloadDeDados extends AsyncTask<String, Void, String>{

        @Override
        protected void onPostExecute(String s) {
            leFaturasDeJSONString(s, pokemonList);
            newGame();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: "+strings[0]);
            String json = downloadRSS(strings[0]);
            if (json == null){
                Log.e(TAG, "doInBackground: Erro baixando JSON");
            }


            return json;
        }

        private String downloadRSS(String urlString){
            StringBuilder jsonString = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int resposta  = connection.getResponseCode();
                Log.e(TAG, "downloadRSS: Código de resposta: "+resposta );

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader (connection.getInputStream())
                );

                int charsLidos;
                char[] InputBuffer = new char[500];
                while(true){
                    charsLidos = reader.read(InputBuffer);
                    if(charsLidos<0){
                        break;
                    }
                    if(charsLidos>0){
                        jsonString.append(String.copyValueOf(InputBuffer, 0, charsLidos));
                    }
                }
                reader.close();
                return jsonString.toString();
            }
            catch (MalformedURLException e){
                Log.e(TAG, "downloadRSS: URL é invalido" + e.getMessage());

            } catch (IOException e) {
                Log.e(TAG, "downloadRSS: Ocorreu um erro de IO ao baixar dados: "+e.getMessage() );

            }
            return null;
        }

    }
}

