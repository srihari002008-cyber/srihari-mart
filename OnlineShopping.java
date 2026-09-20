import java.util.Scanner;

class Login {
    int id;
    String name;

    Login(int id, String name) {
        this.id = id;
        this.name = name;
    }

    void display() {
        System.out.println("\n===== LOGIN DETAILS =====");
        System.out.println("ID   : " + id);
        System.out.println("Name : " + name);
        System.out.println("Login Successful!");
    }
}

public class OnlineShopping {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("===== ONLINE SHOPPING SYSTEM =====");

        System.out.print("Enter User ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter User Name: ");
        String name = sc.nextLine();

        Login user = new Login(id, name);
        user.display();

        sc.close();
    }
}