package r92.se.br.breja.presenter;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import r92.se.br.breja.activity.DetailActivity;
import r92.se.br.breja.constants.MyConstants;
import r92.se.br.breja.fragments.CatalogFragment;
import r92.se.br.breja.interfaces.CatalogPresenterImp;
import r92.se.br.breja.interfaces.CatalogViewImp;
import r92.se.br.breja.model.Beer;
import r92.se.br.breja.service.Service;
import r92.se.br.breja.util.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogPresenter implements CatalogPresenterImp{

    private CatalogViewImp catalogView;
    private Service service;

    private int page = 1;
    private Integer position;
    private List<Beer> beerList;

    public CatalogPresenter(CatalogViewImp catalogView){
        this.catalogView = catalogView;
        this.beerList = new ArrayList<>();
        this.service = new Service();
    }

    public void increasePage(){
        page++;
        getBeerList(page);
    }

    public void onStart(){
        getBeerList(page);
    }

    public void onResume(){
        if(position != null){
            catalogView.updateItemList(position);
        }
    }

    public void isVisibleToUser(){
        catalogView.updateAllBeerList();
    }

    public void updateBeerList(List<Beer> beerList){
        this.beerList.addAll(beerList);
        catalogView.updateBeerList();
    }

    public void showProgress(){
        catalogView.showProgress();
    }

    public void dismissProgress(){
        catalogView.dismissProgress();
    }

    public List<Beer> getBeerList() {
        return beerList;
    }

    public void onItemClick(int position){
        Util.updateFavoriteList(beerList.get(position).getId(), catalogView.getContext());
        catalogView.updateItemList(position);
    }

    public void onCardClick(int position){
        this.position = position;

        Intent intent = new Intent(catalogView.getContext(), DetailActivity.class);
        intent.putExtra(MyConstants.DETAIL_KEY, new Gson().toJson(beerList.get(position), Util.getBeerType()));
        intent.putExtra(MyConstants.DETAIL_KEY_SHOW_FLOAT, true);
        catalogView.getContext().startActivity(intent);
    }

    public boolean isBeerFavorite(Integer id){
        return Util.isBeerFavorite(id,catalogView.getContext());
    }

    public void getBeerList(int page){
        showProgress();
        service.getBeerList(page).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()){
                    List<Beer> beerList = (List<Beer>) response.body();
                    updateBeerList(beerList);
                    dismissProgress();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Util.log("Fail");
            }
        });
    }
}
