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
		{City{2, 30}, City{3, 40}},
		{City{0, 15}, City{1, 25},
     City{8, 70}},
		{City{1, 55}, City{0, 20}, 
     City{5, 15}},
		{City{1, 10}, City{5, 50}},
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
