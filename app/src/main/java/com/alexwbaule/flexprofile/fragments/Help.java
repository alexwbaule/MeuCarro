package com.alexwbaule.flexprofile.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alexwbaule.flexprofile.BuildConfig;
import com.alexwbaule.flexprofile.MeuCarroApplication;
import com.alexwbaule.flexprofile.R;

/**
 * Created by alex on 09/07/15.
 */
public class Help extends BaseFragment {
    private FloatingActionButton fab;
    private WebView webView;
    private final String TextBody = "Dados do Aplicativo:\nPackage: " + BuildConfig.APPLICATION_ID + "\nVersão: " + BuildConfig.VERSION_NAME + "\nBuild: " + BuildConfig.VERSION_CODE + "\n\n ";

    public boolean BackNavigation() {
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);

        webView = view.findViewById(R.id.webView);
        final ProgressBar pgbar = view.findViewById(R.id.progressBar);
        final TextView pbar_txt = view.findViewById(R.id.pbar_loading);

        webView.getSettings().setUserAgentString("MeuCarroApp (appid: " + BuildConfig.APPLICATION_ID + " - version: " + BuildConfig.VERSION_NAME + " - build: " + BuildConfig.VERSION_CODE + ") - Webview Helper");

        webView.getSettings().setJavaScriptEnabled(true);
        pgbar.setVisibility(View.VISIBLE);
        pbar_txt.setVisibility(View.VISIBLE);

        pbar_txt.setText(MeuCarroApplication.getInstance().getString(R.string.carregando));

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pgbar.setProgress(progress * 1000);
            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pgbar.setVisibility(View.GONE);
                pbar_txt.setVisibility(View.GONE);
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/help/index.html");
            }
        });
        //webView.loadUrl("file:///android_asset/help/index.html");
        webView.loadUrl("https://www.tuxviper.com.br/meucarro");

        fab = view.findViewById(R.id.fab_help);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "meucarro@tuxviper.com.br", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Meu Carro App");
                emailIntent.putExtra(Intent.EXTRA_TEXT, TextBody);
                startActivity(Intent.createChooser(emailIntent, "Enviar Feedback"));
            }
        });

        return view;
    }
}
