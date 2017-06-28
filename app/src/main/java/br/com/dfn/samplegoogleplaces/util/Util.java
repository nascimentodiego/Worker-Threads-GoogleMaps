package br.com.dfn.samplegoogleplaces.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Util {

	public static boolean isConnected(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			Toast.makeText(ctx, "Sem dados de conexão", Toast.LENGTH_LONG)
					.show();
			return false;
		}
	}
	
	public static InputStream getStream(String pUrl) throws Exception{
		URL url = new URL(pUrl);
		HttpURLConnection conexao = 
				(HttpURLConnection) url.openConnection();
		conexao.setRequestMethod("GET");
		conexao.setDoInput(true);
		conexao.setConnectTimeout(1500);
		
		if(conexao.getResponseCode() == HttpURLConnection.HTTP_OK){
			return conexao.getInputStream();
		}else{
			return null;
		}
	}
	
	public static String streamToString(InputStream input) throws IOException {
		byte[] bytes = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int lidos;
		while ( (lidos = input.read(bytes))>0) {
			baos.write(bytes,0,lidos);
		}
		
		return new String(baos.toByteArray());
	}
	
	
	

}
