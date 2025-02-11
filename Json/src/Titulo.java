import java.util.ArrayList;

public class Titulo {
    String titulo;
    ArrayList <Heroe> heroes;

    public Titulo(String titulo) {
        this.titulo = titulo;
        heroes= new ArrayList<>();
    }

    public boolean add(Heroe heroe) {
        return heroes.add(heroe);
    }

    public ArrayList<Heroe> getHeroes(){
        return heroes;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
