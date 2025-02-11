public class Heroe {
    String heroe;
    String nombre;
    String link;
    String img;
    int tamanio;

    public Heroe(String heroe, String nombre, String link, String img, int tamanio) {
        this.heroe = heroe;
        this.nombre = nombre;
        this.link = link;
        this.img = img;
        this.tamanio = tamanio;
    }

    public String getHeroe() {
        return heroe;
    }

    public void setHeroe(String heroe) {
        this.heroe = heroe;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }
}
