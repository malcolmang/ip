public class Duke {
    public static Display window = new Display();
    public static void main(String[] args) {
        window.print("Hey, welcome!\nWhat would you like to do today?");
        while(!echo().toLowerCase().equals("bye")){

        }
        window.print("Alright, see you around then!");
    }
    private static String echo(){
        String input = window.getInput();
        window.print(input);
        return input;
    }

}
