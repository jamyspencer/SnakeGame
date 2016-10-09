//Jamy Spencer 001
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;
import static java.lang.Math.abs;



public class Snake{

	static int RIGHT = 0,LEFT = 2, UP = 3, DOWN = 1;

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
	static int RIGHT = 0,LEFT = 2, UP = 3, DOWN = 1;
	public int segments = 1;
	public int direction = RIGHT;//0=right, 1=down , 2=left, 3=up
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
			if(direction == RIGHT && dirs[LEFT]){
				set = true;
				x--;
			}
			else if(direction == DOWN && dirs[3]){
				set = true;
				y++;
			}
			else if(direction == LEFT && dirs[RIGHT]){
				set = true;
				x++;
			}
			else if(direction == UP && dirs[DOWN]){
				set = true;
				y--;
			}
		}
		else{
			if(tail.last.xpos > x && dirs[LEFT]){
				set = true;
				x--;
			}
			else if(tail.last.xpos < x && dirs[RIGHT]){
				set = true;
				x++;
			}
			else if(tail.last.ypos < y && dirs[UP]){
				set = true;
				y++;
			}
			else if(tail.last.ypos > y && dirs[DOWN]){
				set = true;
				y--;
			}
		}
		if(!set){
			if(dirs[RIGHT]) x++;
			else if(dirs[DOWN]) y--;
			else if(dirs[LEFT]) x--;
			else if(dirs[UP]) y++;
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
				if(direction == RIGHT) temp.xpos++;
				else if(direction == DOWN) temp.ypos--;
				else if(direction == LEFT) temp.xpos--;
				else if(direction == UP) temp.ypos++;
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

			if(x == temp.xpos - 1 && y == temp.ypos) dirs[RIGHT] = false;
			if(x == temp.xpos + 1 && y == temp.ypos) dirs[LEFT] = false;
			if(y == temp.ypos + 1 && x == temp.xpos) dirs[DOWN] = false;
			if(y == temp.ypos - 1 && x == temp.xpos) dirs[UP] = false;
			temp = temp.next;
			if(temp == check) temp = temp.next;
		}
		//sets direction to false if it would go off the board
		if(x == 9) dirs[RIGHT] = false;
		if(x == 0) dirs[LEFT] = false;
		if(y == 9) dirs[UP] = false;
		if(y == 0) dirs[DOWN] = false;

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

		if(hx > apple[0]) xTowardApple = LEFT;
		else if(hx< apple[0]) xTowardApple = RIGHT;
		if(hy > apple[1]) yTowardApple = DOWN;
		else if(hy < apple[1]) yTowardApple = UP;

		while(temp != null){//assign value to indicators that crashes are more likely to happen
			if(temp.xpos > hx){
				dirChecks[LEFT]--;
				if(abs(temp.ypos - hy) < 7){dirChecks[RIGHT]++;}
				if(temp.xpos - hx > 7 ){dirChecks[RIGHT]-=2;}
			}
			else if(hx > temp.xpos){
				dirChecks[RIGHT]--;
				if(abs(temp.ypos - hy) < 7) {dirChecks[LEFT]++;}
				if(hx - temp.xpos > 7) {dirChecks[LEFT]-=2;}
			}
			else {dirChecks[LEFT]--; dirChecks[RIGHT]--;}
			if(temp.ypos > hy){
				dirChecks[DOWN]--;
				if(abs(temp.xpos - hx) < 7){dirChecks[UP]++;}
				if (temp.ypos - hy > 7) {dirChecks[UP]-=2;}
			}
			else if (hy > temp.ypos){
				dirChecks[UP]--;
				if(abs(temp.xpos - hx) < 7){dirChecks[DOWN]++;}
				if(hy - temp.ypos > 7){dirChecks[DOWN]-=2;}
			}
			else {dirChecks[UP]--; dirChecks[DOWN]--;}
			temp = temp.next;
		}

		//add a little paranoia about the sides if the apple isn't there
		if(apple[0] < 9 && head.xpos > 7) dirChecks[RIGHT] += (segments*1.25);
		else if(apple[0] < 8 && head.xpos > 6) dirChecks[RIGHT] += (segments*.66);
		else if (apple[0] > 0 && head.xpos < 2) dirChecks[LEFT] += (segments*1.25);
		else if (apple[0] > 1 && head.xpos < 3) dirChecks[LEFT] += (segments*.66);
		if(apple[1] < 9 && head.ypos > 7) dirChecks[UP] += (segments*1.25);
		else if(apple[1] < 8 && head.ypos > 6) dirChecks[UP] += (segments*.66);
		else if(apple[1] > 0 && head.ypos < 2) dirChecks[DOWN] += (segments*1.25);
		else if(apple[1] > 1 && head.ypos < 3) dirChecks[DOWN] += (segments*.66);

		//set preferred direction based on which way the apple is and relative number of snake segments
		for(int i = 0; i < 4; i++){
			if(dirChecks[i] < incrementChecker){incrementChecker = dirChecks[i];}
		}
		while(prefDir == -1) {
			if (xTowardApple == LEFT && dirChecks[LEFT] <= incrementChecker) prefDir = LEFT;
			else if (xTowardApple == RIGHT && dirChecks[RIGHT] <= incrementChecker) prefDir = RIGHT;
			else if (yTowardApple == DOWN && dirChecks[DOWN] <= incrementChecker) prefDir = DOWN;
			else if (yTowardApple == UP && dirChecks[UP] <= incrementChecker) prefDir = UP;
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
				else if(i == UP && !set){
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