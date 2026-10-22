import com.nearsky.tv.LiveryDesign;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*;

/** Export precisely the same SVG strings used by the Android app. */
public final class ExportLiveries {
    public static void main(String[] args) throws Exception {
        Path target=Path.of(args.length==0?"NearSky-liveries.zip":args[0]);
        int count=0;
        try(ZipOutputStream zip=new ZipOutputStream(Files.newOutputStream(target))) {
            for(char a='A';a<='Z';a++) for(char b='A';b<='Z';b++) for(char c='A';c<='Z';c++) {
                String code=""+a+b+c;
                zip.putNextEntry(new ZipEntry("liveries/"+code+".svg"));
                Path override=Path.of("app/src/main/assets/liveries/"+code+".svg");
                zip.write(Files.exists(override)?Files.readAllBytes(override):LiveryDesign.svg(code).getBytes(StandardCharsets.UTF_8));zip.closeEntry();count++;
            }
            zip.putNextEntry(new ZipEntry("README.txt"));
            zip.write(("NearSky SVG liveries\n\n24 bundled hand-redrawn standard airline schemes take precedence over the generated artwork. English lettering is enlarged and redrawn/outlined; these are representative illustrations, not official brand masters, aircraft-type drawings or confirmation of the paint on a specific aircraft. Brand names and marks belong to their owners. The rest of the 17,576 AAA-ZZZ identifiers retain clearly labelled original concept designs. This is identifier coverage, not a count of actual airlines or verified liveries.\n\nRebuild the 24 catalogue assets with python3 tools/generate-liveries.py (Python 3 and JDK 17). Regenerate this pack with tools/export-liveries.sh. No external images, fonts or scripts are needed at SVG display time. See the source livery-catalogue.json and docs/LIVERIES.md for references, limitations and lettering license notices.\n").getBytes(StandardCharsets.UTF_8));zip.closeEntry();
        }
        System.out.println("Exported "+count+" SVGs to "+target);
    }
}
