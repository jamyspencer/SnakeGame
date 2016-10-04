//Jamy Spencer 001
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;
import static java.lang.Math.abs;

public class Snake{

	public static void main(String[] args) {
		int length = 1,
				numMoves = 0;
		boolean noApple = true;
		SnakeObj snake = new SnakeObj();
		Scanner input = new Scanner(System.in);
		int[] apple = {-1, -1};
		while(length < 31){

			while(noApple){
				apple = makeApple(input);
				noApple = !snake.check(apple);
				if(apple[0] < 0 || apple[0] > 9 ) noApple = true;
				if(apple[1] < 0 || apple[1] > 9 ) noApple = true;
				if(noApple) System.out.printf("Sorry %d,%d is bad input.\n", apple[0], apple[1]);
			}
			snake.renderScreen(apple);
			snake.navAlgorithm(apple, numMoves);
			snake.move();
			numMoves++;
			if(snake.head.xpos == apple[0] && snake.head.ypos == apple[1]){
				noApple = true;
				length = snake.addSegment();
				System.out.printf("It took %d moves.\n", numMoves);
				numMoves = 0;
			}
			if(noApple) snake.renderScreen(apple);

		}
		System.out.println("You won :)");
		input.close();
	}
	//make apple for the snake to go after
	private static int[] makeApple(Scanner input){

		Random rand = new Random();
		String usrIn ="";
		int[] apple = new int[2];
		boolean go = true;

		while(go) {//ask for input until valid input is received
			try {
				System.out.printf("Do you want a user defined apple, a random apple, or would you like to exit? ");
				usrIn = input.next();
				go = false;
				if (!(usrIn.equals("user") || usrIn.equals("random") || usrIn.equals("exit"))) {
					System.out.printf("Invalid Input, enter user, random or exit.\n");
					go = true;
				}
			} catch (InputMismatchException badKind) {
				System.out.printf("Invalid Input, enter user, random or exit.");
				go = true;
			}
		}
		if(usrIn.equals("user")){
			System.out.printf("Enter an x coordinate(0-9): ");
			apple[0] = input.nextInt();
			System.out.printf("Enter a y coordinate(0-9): ");
			apple[1] = input.nextInt();
		}
		else if (usrIn.equals("random")){
			apple[0] = rand.nextInt(10);
			apple[1] = rand.nextInt(10);
		}
		else System.exit(0);
		return apple;
	}
}
//double linked list snake object
class SnakeObj{
	public int segments = 1;
	public int direction = 1;//0=right, 1=down , 2=left, 3=up
	Segment head;
	Segment tail;

	public SnakeObj(){
		head = new Segment(5,5);
		tail = head;
	}

	public class Segment{
		int xpos;
		int ypos;
		Segment next = null;
		Segment last = null;

		Segment(int x, int y){
			xpos = x;
			ypos = y;
		}
	}
	//adds segment on to snake
	public int addSegment(){
		//0=right, 1=down , 2=left, 3=up
		int x = tail.xpos;
		int y = tail.ypos;
		boolean dirs[] = directionChecker(tail);
		boolean set = false;

		if(head == tail){
			if(direction == 0 && dirs[2]){
				set = true;
				x--;
			}
			else if(direction == 1 && dirs[3]){
				set = true;
				y++;
			}
			else if(direction == 2 && dirs[0]){
				set = true;
				x++;
			}
			else if(direction == 3 && dirs[1]){
				set = true;
				y--;
			}
		}
		else{
			if(tail.last.xpos > x && dirs[2]){
				set = true;
				x--;
			}
			else if(tail.last.xpos < x && dirs[0]){
				set = true;
				x++;
			}
			else if(tail.last.ypos < y && dirs[3]){
				set = true;
				y++;
			}
			else if(tail.last.ypos > y && dirs[1]){
				set = true;
				y--;
			}
		}
		if(!set){
			if(dirs[0]) x++;
			else if(dirs[1]) y--;
			else if(dirs[2]) x--;
			else if(dirs[3]) y++;
		}
		tail.next = new Segment(x,y);
		tail.next.last = tail;
		tail= tail.next;
		segments++;
		return segments;
	}
	//moves the snake
	public void move(){
		Segment temp = tail;

		while(temp != null){
			if(temp == head){
				if(direction == 0) temp.xpos++;
				else if(direction == 1) temp.ypos--;
				else if(direction == 2) temp.xpos--;
				else if(direction == 3) temp.ypos++;
			}
			else{
				temp.xpos = temp.last.xpos;
				temp.ypos = temp.last.ypos;
			}
			temp = temp.last;
		}
	}
	//checks if relative directions are legal to navigate or to add segments
	private boolean[] directionChecker(Segment check){
		boolean dirs[]  = {true, true, true, true};
		Segment temp = head;
		int x = check.xpos;
		int y = check.ypos;

		if(check == head) temp = temp.next;

		while(temp != null){ // sets direction to false if it would cause the snake to eat itself

			if(x == temp.xpos - 1 && y == temp.ypos) dirs[0] = false;
			if(x == temp.xpos + 1 && y == temp.ypos) dirs[2] = false;
			if(y == temp.ypos + 1 && x == temp.xpos) dirs[1] = false;
			if(y == temp.ypos - 1 && x == temp.xpos) dirs[3] = false;
			temp = temp.next;
			if(temp == check) temp = temp.next;
		}
		//sets direction to false if it would go off the board
		if(x == 9) dirs[0] = false;
		if(x == 0) dirs[2] = false;
		if(y == 9) dirs[3] = false;
		if(y == 0) dirs[1] = false;

		return dirs;
	}
	//this method determines which way the snake should go in order to get the apple
	public void navAlgorithm(int[] apple, int numMoves){
		//0=right, 1=down , 2=left, 3=up
		Segment temp = head;
		boolean[] dirs = directionChecker(head);
		boolean set = false;
		int 	xTowardApple  = -1,
				yTowardApple = -1;
		int hx = head.xpos;
		int hy = head.ypos;
		int 	prefDir = -1,
				incrementChecker = 0;
		int[] dirChecks = {0,0,0,0};

		if(hx > apple[0]) xTowardApple = 2;
		else if(hx< apple[0]) xTowardApple = 0;
		if(hy > apple[1]) yTowardApple = 1;
		else if(hy < apple[1]) yTowardApple = 3;

		while(temp != null){//assign value to indicators that crashes are more likely to happen
			if(temp.xpos > hx){
				dirChecks[2]--;
				if(abs(temp.ypos - hy) < 7){dirChecks[0]++;}
				if(temp.xpos - hx > 7 ){dirChecks[0]-=2;}
			}
			else if(hx > temp.xpos){
				dirChecks[0]--;
				if(abs(temp.ypos - hy) < 7) {dirChecks[2]++;}
				if(hx - temp.xpos > 7) {dirChecks[2]-=2;}
			}
			else {dirChecks[2]--; dirChecks[0]--;}
			if(temp.ypos > hy){
				dirChecks[1]--;
				if(abs(temp.xpos - hx) < 7){dirChecks[3]++;}
				if (temp.ypos - hy > 7) {dirChecks[3]-=2;}
			}
			else if (hy > temp.ypos){
				dirChecks[3]--;
				if(abs(temp.xpos - hx) < 7){dirChecks[1]++;}
				if(hy - temp.ypos > 7){dirChecks[1]-=2;}
			}
			else {dirChecks[3]--; dirChecks[1]--;}
			temp = temp.next;
		}

		//add a little paranoia about the sides if the apple isn't there
		if(apple[0] < 9 && head.xpos > 7) dirChecks[0] += (segments*1.25);
		else if(apple[0] < 8 && head.xpos > 6) dirChecks[0] += (segments*.66);
		else if (apple[0] > 0 && head.xpos < 2) dirChecks[2] += (segments*1.25);
		else if (apple[0] > 1 && head.xpos < 3) dirChecks[2] += (segments*.66);
		if(apple[1] < 9 && head.ypos > 7) dirChecks[3] += (segments*1.25);
		else if(apple[1] < 8 && head.ypos > 6) dirChecks[3] += (segments*.66);
		else if(apple[1] > 0 && head.ypos < 2) dirChecks[1] += (segments*1.25);
		else if(apple[1] > 1 && head.ypos < 3) dirChecks[1] += (segments*.66);

		//set preferred direction based on which way the apple is and relative number of snake segments
		for(int i = 0; i < 4; i++){
			if(dirChecks[i] < incrementChecker){incrementChecker = dirChecks[i];}
		}
		while(prefDir == -1) {
			if (xTowardApple == 2 && dirChecks[2] <= incrementChecker) prefDir = 2;
			else if (xTowardApple == 0 && dirChecks[0] <= incrementChecker) prefDir = 0;
			else if (yTowardApple == 1 && dirChecks[1] <= incrementChecker) prefDir = 1;
			else if (yTowardApple == 3 && dirChecks[3] <= incrementChecker) prefDir = 3;
			incrementChecker++;
		}
		if(numMoves < 15) {incrementChecker = 0;}
		else if(numMoves < 30) {incrementChecker = 1;}
		else if(numMoves < 50) {incrementChecker = 2;}

		//set direction that navigates closer to apple if it is a legal direction
		for(int i = 0; i < 4; i++) {
			if (dirs[i] && prefDir == i && dirChecks[i] < incrementChecker) {
				direction = i;
				set = true;
			}
		}
		if(!set) {
			for (int i = 0; i < 4; i++) {
				if(dirs[i]){prefDir = i; set = true;}
				else if(i == 3 && !set){
					System.out.println("Error: Navigation Impossible");
					System.exit(segments);
				}
			}
			for (int i = 0; i < 4; i++) {
				if (dirChecks[i] < dirChecks[prefDir] && dirs[i]) {
					prefDir = i;
				}
			}
			if (dirs[prefDir]) {
				direction = prefDir;
			}
		}
		return;
	}

	//this method returns a boolean value that indicates if the apple location
	//passed to the method is legal (isn't on the snake)
	public boolean check(int[] apple){
		boolean val = true;
		Segment temp = head;
		while(temp != null) {
			if (apple[0] == temp.xpos) {
				if (apple[1] == temp.ypos) val = false;
			}
			temp  = temp.next;
		}
		return val;
	}

	//this method displays the game board
	public void renderScreen(int[] apple){
		char scrn[][] = new char[10][10];
		Segment temp = head;
		temp = temp.next;
		for(int i = 0; i<10; i++){
			for(int j = 0; j < 10; j++){
				scrn[i][j] = '_';
			}
		}
		while(temp != null){
			scrn[temp.ypos][temp.xpos] = '*';
			temp = temp.next;
		}
		scrn[apple[1]][apple[0]] = 'o';
		scrn[head.ypos][head.xpos] = '@';
		for(int i = 9; i >= 0; i--){
			System.out.printf("%c%c%c%c%c%c%c%c%c%c\n", scrn[i][0], scrn[i][1], scrn[i][2],
					scrn[i][3], scrn[i][4], scrn[i][5], scrn[i][6], scrn[i][7], scrn[i][8], scrn[i][9]);
		}
		System.out.println();
	}
}