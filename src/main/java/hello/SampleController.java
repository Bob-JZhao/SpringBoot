package hello;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class SampleController {

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello spring boot!";
    }
   
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }
    
    @RequestMapping("/cache")
    public ResponseEntity<String> cache(
          HttpServletRequest request,
          //input a para millis
          @RequestParam("millis") long lastModifiedMillis,
          //validate the Last-Modified
          @RequestHeader (value = "If-Modified-Since", required = false) Date ifModifiedSince) {
        //system time
        long now = System.currentTimeMillis();
        //period that could be stored in brows
        long maxAge = 20;
        //
        if(ifModifiedSince != null && ifModifiedSince.getTime() == lastModifiedMillis) {
            return new ResponseEntity<String>(HttpStatus.NOT_MODIFIED);
        }

        DateFormat gmtDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

        String body = "<a href=''>点击访问当前链接</a>";
        MultiValueMap<String, String> headers = new HttpHeaders();

        //document modify time
        headers.add("Last-Modified", gmtDateFormat.format(new Date(lastModifiedMillis)));

        //
        headers.add("Date", gmtDateFormat.format(new Date(now)));
        //expire time  http 1.0 support
        headers.add("Expires", gmtDateFormat.format(new Date(now + maxAge)));
        
        headers.add("Cache-Control", "max-age=" + maxAge);
        return new ResponseEntity<String>(body, headers, HttpStatus.OK);
    }
    
}