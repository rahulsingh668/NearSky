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
                zip.write(LiveryDesign.svg(code).getBytes(StandardCharsets.UTF_8));zip.closeEntry();count++;
            }
            zip.putNextEntry(new ZipEntry("README.txt"));
            zip.write(("NearSky original SVG concept liveries\n\n17,576 files cover the complete AAA-ZZZ identifier space. This is code coverage, not a claim that 17,576 airlines exist. Colors and patterns are original concept art, with verified public airline names for the 12 bundled examples. Other codes keep code lettering until a verified name is supplied. Official wordmark typography is not reproduced. Unassigned codes are included deliberately so newly assigned codes are covered. The application uses the same generator on demand.\n\nRegenerate with tools/export-liveries.sh. Artwork requires no external images, fonts or scripts. SVG files are editable in vector editors.\n").getBytes(StandardCharsets.UTF_8));zip.closeEntry();
        }
        System.out.println("Exported "+count+" SVGs to "+target);
    }
}
