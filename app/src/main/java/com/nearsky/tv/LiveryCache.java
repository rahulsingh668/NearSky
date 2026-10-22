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
    private final LruCache<String,Picture> pictures=new LruCache<>(32);
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
}
