import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public class Battleship extends Applet implements Runnable
{
    //Image Variables
    Image place[] = new Image[11];
    Image Ocean, X;
    //Labels to use
    Label lblScore, lblReset, lblMoves, lblOpp, lblPlay, lblPLives, lblWinner, lblPlace;
    //Board Dimensions as integer variables
    int length = 10, width = 10;
    //Variables for player score, number of moves done
    // how many sections of ship the player has left
    // if the player can play, and what ship he must place.
    int Score = 0, Moves = 1, Lives = 0, CanPlay = 0, Ship = 0;
    //2-D grids for opponent's cover for the board
    // and his ships on the board
    int waterfog[][] = new int[length][width];
    int grid[][] = new int[length][width];
    //2-D grids for opponents shots on the player's ships
    // and the player's ships on the board
    int oppshot[][] = new int[length][width];
    int playgrid[][] = new int[length][width];
    //2-D array to list ship combinations to place
    int ShipPlace[][] = new int[6][9];
    
    public void init ()
    {
        setLayout(null);
        setBackground(Color.white);
        //Placing images into the image variables
        for (int z = 0; z <= 10; z++)
        {
            place[z] = getImage(getCodeBase(),z+".PNG");
        }
        Ocean = getImage(getCodeBase(),"water.jpg");
        X = getImage(getCodeBase(),"Xx.png");
        //Initialize reset function to clear the variables of the grids
        // back to default settings, also make the game playable variable
        // to 0 (not able to play until conditions are passed).
        Reset();
        //Insert labels
        lblReset = new Label("Restart Game");
        add(lblReset);
        lblReset.setBounds((length*5),(width*50)+25,100,25);
        
        lblScore = new Label("Sections Hit: 0");
        add(lblScore);
        lblScore.setBounds((length*5),(width*50)+60,100,25);
        
        lblMoves = new Label("Turn: 0");
        add(lblMoves);
        lblMoves.setBounds((length*50)-20,(width*50)+80,100,25);
        
        lblOpp = new Label("Opponent");
        add(lblOpp);
        lblOpp.setBounds((length*45)-20,(width*50)+5,100,25);
        
        lblPlay = new Label("Player");
        add(lblPlay);
        lblPlay.setBounds((length*55)-20,(width*50)+5,100,25);
        
        lblPLives = new Label("Sections Left: 32");
        add(lblPLives);
        lblPLives.setBounds((length*55),(width*50)+60,100,25);
        
        lblWinner = new Label("     ");
        add(lblWinner);
        lblWinner.setBounds((length*50)-40,(width*50)+115,100,25);
        
        lblPlace = new Label("Ship to Place");
        add(lblPlace);
        lblPlace.setBounds((length*100)+20,(width*5)+25,200,25);
        
        //Using two data files for two arrays:
        // one for opponent's ships on a board
        // the other for player ship combinations to set on their board
        try
        {
            BufferedReader filein, fileinP;
            filein = new BufferedReader (new FileReader("GridDATA1.txt"));
            fileinP = new BufferedReader (new FileReader("GridDATA2.txt"));
            for (int i = 0; i < 10; i++)
            {
                String inputLine = filein.readLine();
                StringTokenizer st = new StringTokenizer(inputLine, " ");
                for (int j = 0; j < 10; j++)
                {
                    String eachNumber = st.nextToken();
                    grid[j][i]= Integer.parseInt(eachNumber);
                }
            }
            for (int k = 0; k < 9; k++)
            {
               String inputLineP = fileinP.readLine();
               StringTokenizer stP = new StringTokenizer(inputLineP, " ");
               for (int l = 0; l < 6; l++) 
               {
                    String eachNumberP = stP.nextToken();
                    ShipPlace[l][k]= Integer.parseInt(eachNumberP);
               }
            }
            filein.close();
            fileinP.close();
        }
        catch(IOException catche)
        {
        }
    }
    
    public void run()
    {
    
    }
    
    public boolean mouseDown (Event evt, int x, int y)
    {
        if (CanPlay == 1)
        {
            //Check where the mouse was clicked on the opponent grid
            for (int a = 0; a < length; a++)
            {
                for (int b = 0; b < width; b++)
                {
        
                    if ((x>(50*a))&&(x<(50*(a+1)))&&(y>(50*b))&&(y<(50*(b+1))))
                    {
                        //Check if area clicked has not been clicked previously
                        if (waterfog[a][b]==0)
                        {
                            //Make the area cleared, add one to the moves variable.
                            waterfog[a][b] = 1;
                            Moves++;
                            //Check if the opponent grid has a section
                            // if there is, add to the score
                            if (grid[a][b]>0)
                            {
                                Score++;
                            }
                            //OppPlay has the computer register two random numbers
                            // and play the game like the player, then check the player's lives
                            OppPlay();
                            Lives = LiveCheck();
                            repaint();
                        }
                    }
                }
            }
            //Win Conditions: if Computer gets all the player ships, CP wins
            // If player gets all the opponent's ships first, player wins
            if (Lives == 0)
            {
                lblWinner.setText("Computer Wins!");
            }
            else if (Score == 32)
            {
                lblWinner.setText("Player Wins!");
            }
        }
        else
        {
        //If game isn't in play, check if a given point on the player's board
        // is a good spot for the player to place a ship,
        // if all ships are set, make the game playable
            for (int a = 0; a < length; a++)
            {
                for (int b = 0; b < width; b++)
                {
        
                    if ((x>(50*a)+510)&&(x<(50*(a+1)+510))&&(y>(50*b))&&(y<(50*(b+1))))
                    {
                        CanPlace(a,b);
                        repaint();
                    }
                    Lives = LiveCheck();
                    if (Lives == 32)
                    {
                        CanPlay = 1;
                        lblPlace.setText("Player ready. Play game");
                    }
                }
            }
        }
        //Reset hotspot that puts all variables to default values
        if ((x>(length*4))&&(x<(length*10))&&(y>(width*50))&&(y<(width*56)))
        {
            Reset();
            Score = 0;
            Moves = 1;
            Lives = 0;
            Ship = 0;
            lblWinner.setText("");
            lblPlace.setText("Ship to Place");
            repaint();
        }
        //Rearrange labels according to variable values
        lblScore.setText("Sections Hit: "+Score);
        lblMoves.setText("Turn: "+Moves);
        lblPLives.setText("Sections Left: "+Lives);

    
    return true;
    }
    
    public boolean Reset()
    {
    //Reset function that sets the game to not play until
    // certain conditions are set(player puts down all pieces)
    // also puts the grid values to 0 (default)
        for (int x = 0; x < length; x++)
        {
            for (int y = 0; y < width; y++)
            {
                waterfog[x][y] = 0;
                oppshot[x][y] = 0;
                playgrid[x][y] = 0;
            }
        }
        CanPlay = 0;
        return true;
    }
    
    public boolean CanPlace(int x, int y)
    {
        int parts = 0, checks = 0;
        //Identify the length of the ship
        for(int a = 0; a < 6; a++)
        {
            if(ShipPlace[a][Ship]>0)
            {
                parts++;
            }
        }
        //Check if the ship would go out of bounds
        if (CheckLR(x+parts))
        {
            //Check if there is any pre-existing ship in the area
            // selected before actually setting the ship
            for(int c = 0; c < parts; c++)
            {
                if (playgrid[x+c][y]==0)
                {
                    checks++;
                }
                if (checks==parts)
                {
                    for(int b = 0; b < parts; b++)
                    {
                        playgrid[x+b][y]=ShipPlace[b][Ship];
                    }
                    Ship++;
                }
            }
        }
        return true;
    }
    
    public boolean CheckLR(int e)
    {
    //Function set to check if a part exceeds the edge of the player grid    
        if (e < 0)
        {
            return false;
        }
        else if (e > length)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    public boolean OppPlay()
    {
    //Opponent play function that acts like the player clicking a section
    // on the opponent's grid, though it is very random
        int tx = (int)(length*Math.random()); 
        int ty = (int)(width*Math.random());
        if (oppshot[tx][ty]==0)
        {
            oppshot[tx][ty]=1;
        }
        else
        {
            //Cycles function until the random point on the grid hasn't been used
            OppPlay();
        }
        return true;
    }
    
    public int LiveCheck()
    {
        //Checks player's grid for any ship sections, and if the opponent
        // hasn't hit that area yet, then adds to the variable that will
        // be returned.
        int c = 0;
        for (int a = 0; a < length; a++)
        {
            for (int b = 0; b < width; b++)
            { 
                if (playgrid[a][b]>0)
                {
                    if (oppshot[a][b]==0)
                    {
                        c++;
                    }
                }
            }
        }
        return c;
    }

    public void paint(Graphics g)
    {
        for (int x = 0; x < length; x++)
        {
            for (int y = 0; y < width; y++)
            {
                //Paint the grids of the ships first
                g.drawImage(place[grid[x][y]],(x*50),(y*50),50,50,this);
                g.drawImage(place[playgrid[x][y]],(x*50)+510,(y*50),50,50,this);
                //If a section was not checked by the player cover that area with an image
                if (waterfog[x][y]==0)
                {
                    g.drawImage(Ocean,(x*50),(y*50),50,50,this);
                }
                //If an opponent hit an area already, place an image over there
                if (oppshot[x][y]==1)
                {
                    g.drawImage(X,(x*50)+510,(y*50),50,50,this);
                }
            }
        }
        //Draw the ship player must place to the side.
        if (CanPlay == 0)
        {
            for (int a = 0; a < 6; a++)
            {
                g.drawImage(place[ShipPlace[a][Ship]],(a*50)+1020,0,50,50,this);
            }
        }
    }   
 }
