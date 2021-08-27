# Introduction

I often think that examples to illustrate property testing are quite contrived and not representative of any problem that you might face in real life.
Examples such as reverse a string twice and you still get the same string. My first question is "how do I use it to test my code?".

I also really hate mocking as learning a mocking library is like learning a new language. I would much rather assert on the value returned from functions than the interactions on mock objects.

So I decided to illustrate a slightly more real world example of a place where property testing may be useful. 
This is also somewhat contrived itself as I deliberately chose a problem where it works well, there are definitely places where it works better than others which I will discuss later.

It also utilises final tagless to make the property testing possible while still keeping the useful guarantees provided by pure functions. This performs the same function as mocking libraries while just using regular types.

I am by no means an expert on this so I look forward to hearing other opionions on this.


# Preview

The problem that we are going to tackle to illustrate the ideas is implementing a simple board game. 
This is something I do in my spare time as it is quite a fun problem. 
They are deceptively complex to model and have a lot of scope for learning different concepts, such as artificial intelligence, graphics and asynchronous shared state.

# Nim

This is practically the simplest game I could find with actual player decisions.

## Rules to **Single pile Nim** or **The Subtraction Game**

- At the start of the game there are 12 stones
- The aim of the game is to take the last stone
- Two players take turns to make their move
- Each move, a player may take 1, 2, or 3 marbles

These are the rules to nim. What is more - they are also a strict specification of how our game should behave.
A first attempt at modeling the game without being able to test will be next.

# Players

## scala

```scala
import cats.Show

object Players {

  sealed trait Player
  case object PlayerOne extends Player
  case object PlayerTwo extends Player

  def nextPlayer: Player => Player = {
    case PlayerOne => PlayerTwo
    case PlayerTwo => PlayerOne
  }

  implicit val showPlayer: Show[Player] = {
    case PlayerOne => "Player One"
    case PlayerTwo => "Player One"
  }
}
```
# Program

## scala

```scala
import cats.effect.IO
import cats.implicits._
import scala.io.StdIn.readInt
import Players._

object Program {
  val initialStones = 12

  def nim: IO[Unit] = {
    def play(stones: Int, player: Player): IO[Unit] =
      for {
        _ <- announceStonesLeft(stones)
        move <- getMove(player)
        stonesLeft = stones - move
        _ <-
          if (stonesLeft == 0) announceWinner(player)
          else play(stonesLeft, nextPlayer(player))
      } yield ()
    play(initialStones, PlayerOne)
  }

  def announceStonesLeft(stones: Int): IO[Unit] =
    IO(println(s"Stones left:  $stones"))

  def getMove(player: Player): IO[Int] =
    for {
      _ <- IO(println(show"$player, what is your move?"))
      move <- IO(readInt)
    } yield move

  def announceWinner(player: Player): IO[Unit] =
    IO(println(show"$player wins!"))
    
}
```

# Thoughts on first example

We can try it out and it seems to work, as long as we enter valid moves. It could definitely be improved to disallow invalid move entry. 
If we would like to test it to make sure that it works as per the specification and allow us to more easily make minor modifications while still ensuring it still works correctly we will have to make some changes. 
It is much easier to test values returned from a function than to test that it prints the correct information. 
So our first step is to divide our function into returning the final game state (i.e. the winning player) which we can test, then any function that calls it can print to the console.
We would also like to be able to inject a means by which we can direct the inputs of the players. So we can automate testing for a variety of inputs.

We could use regular unit testing strategies at this point but that has some downsides in this particular case. There are 927 different games that can be played, so we will likely have to choose some arbitrary examples rather than write a test for each case. The examples we choose will not be descriptive and we could even get the expectations wrong. This is a good application for property testing because it will generate many more tests than we are likely to want to write. And the tests themselves will be more general and therefore descriptive of how the program should behave in the general case.

That is, the test:
"The player who plays the last move is the winner" is more descriptive than "If player moves go 3,3,2,1,3 then player 1 wins"



