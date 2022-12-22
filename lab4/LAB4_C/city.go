package main

type City struct {
	index int
	routePrice int
}

type Grades int

const (
	EMPTY Grades = iota
	ANY
	FULL
)

func makeGraph() [][]City {
	return [][]City{
		{City{1, 15}, City{2, 35}},
		{City{0, 20}, City{2, 20}, City{4, 30}},
		{City{0, 35}, City{1, 20}, City{3, 15}},
		{City{2, 15}, City{4, 10}},
		{City{1, 35}, City{3, 10}},
	}
}

func makeCities() []City {
	return []City{
		{0, 0},
		{1, 0},
		{2, 0},
		{3, 0},
		{4, 0},
	}
}
