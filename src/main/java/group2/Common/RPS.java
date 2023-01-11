package group2.Common;


// RPS (ROCK,PAPER,SCISSORS)
public class RPS {

    Choice choice;

    // RPS constructor using enums
    public RPS(String choice) { // 0 = rock, 1 = paper, 2 = scissors
        switch (choice.toLowerCase()) {
            case "rock":
                this.choice = Choice.ROCK;
                break;
            case "paper":
                this.choice = Choice.PAPER;
                break;
            case "scissors":
                this.choice = Choice.SCISSORS;
                break;
            default:
            	this.choice = Choice.ROCK;
            	break;
        }
    }

    public RPS(Choice choice) {
        this.choice = choice;
    }

    public Choice getChoice() {
        return choice;
    }

    // Method that decides winner
    public int winner(RPS other) {
        int winner = 2;
        switch (choice) {
            case ROCK:
                if (other.choice == Choice.SCISSORS) {
                    winner = 0;
                } else if (other.choice == Choice.PAPER) {
                    winner = 1;
                }
                break;
            case PAPER:
                if (other.choice == Choice.ROCK) {
                    winner = 0;
                } else if (other.choice == Choice.SCISSORS) {
                    winner = 1;
                }
                break;
            case SCISSORS:
                if (other.choice == Choice.PAPER) {
                    winner = 0;
                } else if (other.choice == Choice.ROCK) {
                    winner = 1;
                }
                break;
            default:
            	winner = 2;
            	break;
        }
        return winner;
    }
}