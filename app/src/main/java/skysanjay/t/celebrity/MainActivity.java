package skysanjay.t.celebrity;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURL = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    int currentCeleb = 0;
    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;
    Button op1,op2,op3,op4;
    ImageView celebImageView;

    public void celebChoosen(View view){
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(getApplicationContext(),"Correct", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(),"Wrong! It was " +
                    celebNames.get(currentCeleb), Toast.LENGTH_SHORT).show();
        }
        nextQuestion();
    }

    public class DownloadImage extends AsyncTask<String , Void , Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public class DownloadCelebrity extends AsyncTask<String, Void , String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL newURL;
            HttpURLConnection urlConnection = null;
            try {
                newURL = new URL(urls[0]);
                urlConnection = (HttpURLConnection) newURL.openConnection();
                InputStream input = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);
                int data = reader.read();

                while (data != -1){
                    char current = (char)  data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void nextQuestion(){
        try {
            Random random = new Random();
            currentCeleb = random.nextInt(celebURL.size());

            DownloadImage downloadImage = new DownloadImage();
            Bitmap celebImage = downloadImage.execute(celebURL.get(currentCeleb)).get();
            celebImageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnsLocation;
            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(currentCeleb);
                } else {
                    incorrectAnsLocation = random.nextInt(celebURL.size());
                    while (incorrectAnsLocation == currentCeleb) {
                        incorrectAnsLocation = random.nextInt(celebURL.size());
                    }
                    answers[i] = celebNames.get(incorrectAnsLocation);
                }
            }
            op1.setText(answers[0]);
            op2.setText(answers[1]);
            op3.setText(answers[2]);
            op4.setText(answers[3]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        celebImageView = findViewById(R.id.CelebImageView);
        op1 = findViewById(R.id.option1);
        op2 = findViewById(R.id.option2);
        op3 = findViewById(R.id.option3);
        op4 = findViewById(R.id.option4);

        DownloadCelebrity celebrity = new DownloadCelebrity();
        String result = null;
        try {
            String debug = "Code starts here";
            Log.i("Debug",debug);
            result = celebrity.execute("https://www.imdb.com/list/ls052283250/").get();
            String[] split = result.split("<div id=\"sidebar\">");
            String newSplit = split[0];

            //getting urls
            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(newSplit);
            int counter = 1;
            while (m.find()) {
                if (counter>5){
                    celebURL.add(m.group(1));
                }
                counter++;
            }
            //getting names
            p = Pattern.compile("<img alt=\"(.*?)\"");
            m = p.matcher(newSplit);
            while (m.find()) {
                celebNames.add(m.group(1));
            }
            nextQuestion();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}