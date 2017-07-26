package com.example.pc.mediaapp;

import android.app.ProgressDialog;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private static final String AUDIO_URL="http://sites.google.com/site/ubiaccessmobile/sample_audio.amr";

    private MediaPlayer mMediaPlayer;
    private int mPlayBackPos = 0;   //재생 위치 저장변수
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPlay = (Button)findViewById(R.id.btnPlay);
        Button btnStop = (Button)findViewById(R.id.btnStop);
        Button btnReplay = (Button)findViewById(R.id.btnReplay);

        mVideoView = (VideoView)findViewById(R.id.videoView);
        Button btnVideo = (Button)findViewById(R.id.btnVideo);

        btnPlay.setOnClickListener(btnClick);
        btnStop.setOnClickListener(btnClick);
        btnReplay.setOnClickListener(btnClick);
        btnVideo.setOnClickListener(btnClick);
    }
    //미디어 재생
    private void playAudio(String url){
        //기존에 재생하고 있는 미디어가 있다면 먼저 재생을 해제
        killMediaPlayer();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepare(); //미디어 재생시 준비작업 필요 -> 반드시 호출해야함
            mMediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void playAssetAudio(){

        killMediaPlayer();

        try {
            AssetFileDescriptor afd = getAssets().openFd("audio/Kalimba.mp3");
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //미디어 재생해제
    private void killMediaPlayer(){
        if(mMediaPlayer != null){
            try{
                mMediaPlayer.release();
            }catch (Exception e){}
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                case R.id.btnPlay:
                   //playAudio(AUDIO_URL);
                    playAssetAudio();
                    break;

                case R.id.btnStop:
                    if( mMediaPlayer != null && mMediaPlayer.isPlaying() ){
                        //재생되던 마지막 위치를 저장
                        mPlayBackPos = mMediaPlayer.getCurrentPosition();
                        mMediaPlayer.pause();
                        Toast.makeText(MainActivity.this, "재생 중지",Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.btnReplay:
                    if(mMediaPlayer !=null && !mMediaPlayer.isPlaying()){
                        mMediaPlayer.start();
                        //정지된 곳으로 이동
                        mMediaPlayer.seekTo(mPlayBackPos);
                    }
                    break;

                case R.id.btnVideo:
                    String vUrl = "http://sites.google.com/site/ubiaccessmobile/sample_video.mp4";

                    MediaController mc = new MediaController(MainActivity.this);
                    mVideoView.setMediaController(mc);
                    mVideoView.setVideoURI(Uri.parse(vUrl));
                    mVideoView.requestFocus();

                    final ProgressDialog prd = new ProgressDialog(MainActivity.this);
                    prd.setMessage("비디오 재생준비중");
                    prd.setCancelable(false);
                    prd.show();

                    mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            prd.dismiss();
                            mp.start();
                            Toast.makeText(MainActivity.this, "비디오 재생", Toast.LENGTH_LONG).show();
                        }
                    });

                    mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Toast.makeText(MainActivity.this, "비디오 재생 종료", Toast.LENGTH_LONG).show();
                        }
                    });

                    break;
            }//end switch
        }
    };//end OnClickListener
}
