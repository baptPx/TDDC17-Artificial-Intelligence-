
(define (problem robot-shakey-problem)
	(:domain robot-shakey)
	(:objects
		r1 r2 r3 	- room
		b1 			- box
		robot 		- robot
		gr gl 		- gripp 	;
	)

	(:init
		(robot-at robot r2)
		(adjacent r1 r2) (adjacent r2 r3) 
		(door-wide r1 r2) (door-wide r2 r3)
		(lightswitch r1) (lightswitch r2) (lightswitch r3)
		(box-in b1 r1)
	)
	(:goal
		(and
			(light-on r1) (light-on r2) (light-on r3)
		)
	)
)