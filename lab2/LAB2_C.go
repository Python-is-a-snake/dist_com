package main
import (
	"fmt"
)

func main() {
	var ch = make(chan int, 1)
  vals := []int{10, 28, 75, 100, 80}
	startFight(vals, 0, len(vals)-1, ch)
	var winner = <-ch
	fmt.Print("THIS MONK HAS WON : ", initMonk(winner))
}

func initMonk(id int) string {
	name := " "
	if id%2 == 0 {
		name = "Chin Chan Chong"
	} else {
		name = "Suie The Big"
	}
	return name
}

func computeWinner(monks []int, start int, end int) int {
	index := start
	if monks[index] < monks[end] {
		index = end
	}
	return index
}

func startFight(chCount []int, start int, end int, ch chan <- int) {
	winner := start
	if end-start < 2 {
		winner = computeWinner(chCount, start, end)
	} else {
		center := start/2 + end/2
		var chOne = make(chan int, 2)
		go startFight(chCount, start, center, chOne)
		startFight(chCount, center, end, chOne)
		firstW := <-chOne
		secondW := <-chOne
		winner = computeWinner(chCount, firstW, secondW)
	}
	ch <- winner
}
