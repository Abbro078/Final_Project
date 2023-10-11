// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public void print_number(double x){
        System.out.println("******\nvalue is "+x+"\n******");
    }
    public double guess_number(){
        return (int)(Math.random()*100);
    }
    public static void main(String[] args) {
        Main m = new Main();
        double guess = m.guess_number();
        m.print_number(guess);
    }
}