import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.SAXException;

public class crawler {
    static int tope=200;
    static Scanner teclado = new Scanner(System.in);
    static final Pattern semilla = Pattern.compile("https:\\/\\/es\\.wikipedia\\.org\\/wiki\\/([A-Za-z0-9\\_\\-\\.\\(\\)ñáéíóúüÑÁÉÍÓÚÜ]+)"); // Semilla
    static Matcher mat;
    static final Pattern filtro2 = Pattern.compile("href=\"\\/wiki\\/.*:.*\"");
    static ArrayList<String> url_lista = new ArrayList<>();
    static Set<String> url_recorridas = new HashSet<>();
    static String[] reemplazaroriginal = {"á", "é", "í", "ó", "ú", "Á", "É", "Í", "Ó", "Ú", "ñ", "Ñ", "ü", "Ü"};

    static String[] reemplazar = {"%C3%A1", "%C3%A9", "%C3%AD", "%C3%B3", "%C3%BA", "%C3%81", "%C3%89", "%C3%8D",
            "%C3%93", "%C3%9A", "%C3%B1", "%C3%91", "%C3%BC", "%C3%9C"};
    static Pattern filtro3 = Pattern.compile("href=\"(\\/wiki\\/([A-Za-z0-9\\_\\-\\.\\(\\)ñáéíóúüÑÁÉÍÓÚÜ]+))");

    public static String GeneroHTML(URL url) throws TikaException, IOException, SAXException {
        String HTML = "";
        InputStream input = TikaInputStream.get(url);
        ToXMLContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        HtmlParser parser = new HtmlParser();
        parser.parse(input, handler, metadata);
        input.close();
        HTML = handler.toString();
        return HTML;

    }

    public static void WriteFile(File f, String contenido) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
            bw.write(contenido);
        }
    }

    public static int HttpResponse(URL url) {
        int r = -1;
        try {
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("GET");
            huc.connect();
            r = huc.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public static String AplicaFiltros(String html) {
        for (int i = 0; i < reemplazar.length; i++) {
            Pattern semilla4 = Pattern.compile(reemplazar[i]);
            Matcher mat4 = semilla4.matcher(html);
            html = mat4.replaceAll(reemplazaroriginal[i]);
        }

        Matcher mat3 = filtro2.matcher(html);
        html = mat3.replaceAll(" ");

        return html;

    }

    public static void fill_URL_array(String html) {
        Matcher mat2 = filtro3.matcher(html);
        while (mat2.find()) {
            String visited = "https://es.wikipedia.org" + mat2.group(1);
            if (!url_recorridas.contains(visited) && !url_lista.contains(visited))
                url_lista.add(visited);
        }
    }


    public static void crawling() throws IOException, TikaException, SAXException {
        String link = url_lista.get(0);
        mat = semilla.matcher(link);
        if (mat.matches()) {
            String title_link = mat.group(1);



            File file = new File("C:\\Users\\Carlitoxere\\OneDrive\\Escritorio\\Universidad\\4º Año\\REC-INF/Archivos/" + title_link + ".doc");
            URL url = new URL(link); //Creo un objeto de la clase URL.
            int r = HttpResponse(url);
            if (!file.exists() && !url_recorridas.contains(link) && r != 404) {  


                url_recorridas.add(link);// Añado el enlace a la lista de visitados

                String html = GeneroHTML(url);//Descargo el codigo HTML de una pagina web a través de su URL
                String content = new Tika().parseToString(url); //Descargo el contenido de la web como cadena de texto

                WriteFile(file, content);// En content tengo todo el contenido de la web y lo escribo en el doc
                html = AplicaFiltros(html);// Al codigo HTML le aplico los filtros
                fill_URL_array(html);// Extrae los enlaces a otras pag web contenida en la pag web dada



                System.out.println("DESCARGANDO " + title_link + "....");

            }else {
                if (!file.exists())
                    System.out.println("ERROR: EL ARCHIVO " + link + " YA EXISTE");
                else if (!url_recorridas.contains(link))
                    System.out.println("ERROR: " + link + " YA HA SIDO VISITADO");
                else if (r != 404)
                    System.out.println(
                            "ERROR: LA ENTRADA DE WIKIPEDIA " + link + " NO EXISTE O AÚN NO HA SIDO ESCRITA");
            }
            url_lista.remove(0);
            tope --;



        }
    }


        public static void main (String[]args) throws IOException, TikaException, SAXException {
              String idioma;

            System.out.println("Introduce el idioma deseado \n Español -> es\n Inglés -> en\n Frances -> fr");
            idioma = teclado.nextLine();

            Pattern semilla = Pattern.compile("https:\\/\\/"+idioma+"\\.wikipedia\\.org\\/wiki\\/([A-Za-z0-9\\_\\-\\.\\(\\)ñáéíóúüÑÁÉÍÓÚÜ]+)"); // Semilla

            if (idioma.equals("es")) {

                System.out.println("Introduce el link de la Wikipedia");

                int r = 0;
                String link;
                boolean escape;
                do {
                    link = teclado.nextLine();
                    link = AplicaFiltros(link);
                    mat = semilla.matcher(link);
                    if (mat.matches()) { //Si coincide con la estructura de link de wikipedia
                        URL url1 = new URL(link);

                        r = HttpResponse(new URL(link));
                        if (r != 404) { //Si el articulo existe

                            url_lista.add(link); //Introduzco e link en la lista de URL.
                            mat = semilla.matcher(link);// DUDA SI PONER O NO
                            //teclado.close();
                            while (url_lista.size() > 0&& tope!=0) {
                                crawling();

                            }


                        } else {
                            System.out.println("La pagina web no existe");
                        }


                    } else {
                        System.out.println("Link introducido erróneo");
                    }
                } while (r != 0 && r != 404);

            }else{
                System.out.println("Este crawler solo admite paginas url en espanol");
            }
        }
    }


