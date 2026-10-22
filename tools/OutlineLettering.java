import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.nio.file.*;
import java.util.*;
/** Build-time lettering outlines. No font files or desktop APIs are needed by the APK. */
public final class OutlineLettering {
    public static void main(String[] args)throws Exception {
        if(args.length==2&&args[0].equals("--batch")){
            for(String job:Files.readAllLines(Path.of(args[1])))if(!job.isBlank())render(job.split("\t",-1));
        }else render(args);
    }
    private static void render(String[] args)throws Exception {
        Font font=Font.createFont(Font.TRUETYPE_FONT,Path.of(args[0]).toFile()).deriveFont(100f);
        Shape shape=font.createGlyphVector(new FontRenderContext(null,true,true),args[1]).getOutline();
        Rectangle2D box=shape.getBounds2D();
        PathIterator it=shape.getPathIterator(null);double[] q=new double[6];StringBuilder path=new StringBuilder();
        while(!it.isDone()){
            int kind=it.currentSegment(q);
            path.append(new String[]{"M","L","Q","C","Z"}[kind]);
            int n=kind==0||kind==1?2:kind==2?4:kind==3?6:0;
            for(int i=0;i<n;i++)path.append(String.format(Locale.ROOT,"%.3f ",q[i]));
            it.next();
        }
        String svg=String.format(Locale.ROOT,"<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%.3f\" height=\"%.3f\" viewBox=\"0 0 %.3f %.3f\"><g transform=\"translate(%.3f %.3f)\"><path d=\"%s\" fill=\"%s\"/></g></svg>",box.getWidth()+2,box.getHeight()+2,box.getWidth()+2,box.getHeight()+2,1-box.getX(),1-box.getY(),path,args[2]);
        Files.writeString(Path.of(args[3]),svg);
    }
}
