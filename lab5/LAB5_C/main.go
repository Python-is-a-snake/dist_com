package main

import (
	"fmt"
	"math/rand"
	"sync"
	"time"
)

const TestArraysCount = 3
const TestArraySize = 10
const TestValueBound = 3

var random = rand.New(rand.NewSource(time.Now().Unix()))

type ArraysGroup struct {
	arrays [][]int
	sync.WaitGroup
}

func createArraysGroup(arraysCount, arraySize, valueBound int) *ArraysGroup {
	return &ArraysGroup{arrays: initArraysGroup(arraysCount, arraySize, valueBound)}
}

func initArraysGroup(arraysCount, arraySize, valueBound int) [][]int {
	arrayList := make([][]int, arraysCount, arraySize)

	for i := 0; i < arraysCount; i++ {
		arrayList[i] = generateArray(arraySize, valueBound)
	}

	return arrayList
}

func generateArray(size, valueBound int) []int {
	array := make([]int, size)

	for i := 0; i < size; i++ {
		array[i] = random.Intn(valueBound)
	}

	return array
}

func printArraysGroup(arraysGroup *ArraysGroup) {
	for i := 0; i < len(arraysGroup.arrays); i++ {
		fmt.Println(arraysGroup.arrays[i])
	}

	fmt.Println()
}

func performTask(arraysGroup *ArraysGroup, waitGroup *sync.WaitGroup, arraysCount, arraySize, valueBound int) {
	toStop := false

	for !toStop {
		waitGroup.Add(arraysCount)

		for i := 0; i < arraysCount; i++ {
			go changeElement(arraysGroup, waitGroup, i, arraySize, valueBound)
		}

		waitGroup.Wait()

		if checkRule(arraysGroup, arraysCount) {
			toStop = true
			fmt.Println(" THE SUM IS EQUAL ")
		}

		printArraysGroup(arraysGroup)
	}
}

func abs(number int) int {
	result := number

	if result < 0 {
		result *= -1
	}

	return result
}

func changeElement(arraysGroup *ArraysGroup, waitGroup *sync.WaitGroup, currentArrayIndex, arraySize, valueBound int) {
	elemToChange := random.Intn(arraySize)
	sign := 1

	if random.Intn(2) == 1 {
		sign = -1
	}

	if abs(arraysGroup.arrays[currentArrayIndex][elemToChange]) < valueBound {
		arraysGroup.arrays[currentArrayIndex][elemToChange] += sign
	}

	time.Sleep(100 * time.Millisecond)

	waitGroup.Done()
}

func checkRule(arraysGroup *ArraysGroup, arraysCount int) bool {
	sumArray := make([]int, arraysCount)

	for i := 0; i < arraysCount; i++ {
		currentArraySum := 0

		for _, j := range arraysGroup.arrays[i] {
			currentArraySum += j
		}

		sumArray[i] = currentArraySum
	}

	return checkSumEquality(sumArray)
}

func checkSumEquality(array []int) bool {
	fmt.Println("Sum: ", array)

	for i := range array {
		if array[0] != array[i] {
			return false
		}
	}

	return true
}

func main() {
	arr := createArraysGroup(TestArraysCount, TestArraySize, TestValueBound)

	group := new(sync.WaitGroup)
	performTask(arr, group, TestArraysCount, TestArraySize, TestValueBound)
}
