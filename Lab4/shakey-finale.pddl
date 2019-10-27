(define (domain robot-shakey)
	(:requirements :strips :equality :typing :adl)
	(:types
		box 		
		object 		
		room 	
		robot 	
		gripp
	)

	(:predicates
		(adjacent		?r1 ?r2 - room)	
		(robot-at 		?s - robot ?r - room)
		(box-in			?b - box ?r - room)	
		(lightswitch		?r - room)	
		(door-wide	?r1 ?r2 - room)			
		(object		?o - object ?r - room)
		(light-on	?r - room)				
		(holding1   ?g - gripp)
		(holding2   ?g - gripp)
		(empty		?g - gripp)
	)

	(:action robot-move
		:parameters (?ro - robot ?r1 ?r2 - room)
		:precondition (and 
						(or (adjacent ?r1 ?r2)
							(adjacent ?r2 ?r1))
						(robot-at ?ro ?r1)
					)
		:effect (and
					(robot-at ?ro ?r2)
					(not (robot-at ?ro ?r1))
				)
	)

	(:action push-box
		:parameters (?ro - robot ?b - box ?r1 ?r2 - room)
		:precondition (and
						(or 
							(door-wide ?r1 ?r2) 
							(door-wide ?r2 ?r1)
						)
						(box-in ?b ?r1)
						(robot-at ?ro ?r1)
					)

		:effect (and
					(box-in ?b ?r2)
					(not (box-in ?b ?r1))
					(robot-at ?ro ?r2)
					(not (robot-at ?ro ?r1))
				)
	)

	(:action switch-on
		:parameters (?ro - robot ?b - box ?r - room)
		:precondition (and
						(robot-at ?ro ?r)
						(box-in ?b ?r)
						(lightswitch ?r)
						(not (light-on ?r))
					)

		:effect (and
					(light-on ?r)
				)
	)

)