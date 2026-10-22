package com.nearsky.tv;

import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.RectF;
import android.util.LruCache;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import java.io.IOException;
import java.io.InputStream;

final class LiveryCache {
    private final LruCache<String,Picture> pictures=new LruCache<>(64);
    private final AssetManager assets;
    LiveryCache(AssetManager assets){this.assets=assets;}
    private static String readSvg(InputStream input)throws IOException{
        java.io.ByteArrayOutputStream output=new java.io.ByteArrayOutputStream();
        byte[] buffer=new byte[4096];int n;
        while((n=input.read(buffer))!=-1){if(output.size()+n>2097152)throw new IOException("SVG override too large");output.write(buffer,0,n);}
        return output.toString("UTF-8");
    }
    void draw(Canvas canvas,String code,String airlineName,RectF bounds){
        String operator=LiveryDesign.normalized(code);
        String key=operator+":"+AirlineNames.clean(airlineName);
        Picture picture=pictures.get(key);
        if(picture==null){
            if(!operator.isEmpty())try(InputStream input=assets.open("liveries/"+operator+".svg")){
                picture=SVG.getFromString(LiveryDesign.withName(readSvg(input),operator,airlineName)).renderToPicture(1200,440);
            }catch(IOException|SVGParseException ignored){/* Missing or invalid override: retain code coverage with the generator. */}
            if(picture==null)try{
                picture=SVG.getFromString(LiveryDesign.svg(operator,airlineName)).renderToPicture(1200,440);
            }catch(SVGParseException e){throw new IllegalStateException("Invalid bundled livery generator",e);}
            pictures.put(key,picture);
        }
        canvas.drawPicture(picture,bounds);
    }
    boolean drawBrand(Canvas canvas,String code,RectF bounds){
        String operator=LiveryDesign.normalized(code);
        if(!AirlineLiveries.contains(operator))return false;
        String key="brand:"+operator;
        Picture picture=pictures.get(key);
        if(picture==null)try(InputStream input=assets.open("wordmarks/"+operator+".svg")){
            picture=SVG.getFromString(readSvg(input)).renderToPicture();
            pictures.put(key,picture);
        }catch(IOException|SVGParseException ignored){return false;}
        if(picture.getWidth()<=0||picture.getHeight()<=0)return false;
        float scale=Math.min(bounds.width()/picture.getWidth(),bounds.height()/picture.getHeight());
        float height=picture.getHeight()*scale;
        float top=bounds.top+(bounds.height()-height)/2;
        canvas.drawPicture(picture,new RectF(bounds.left,top,bounds.left+picture.getWidth()*scale,top+height));
        return true;
    }
}
