Requires docker compose,

run (docker compose build && docker compose up) to run.

My telegram bot for creating tests for your group

supported Bot commands -

/start - start info

/join - join group by code

/creategroup - create group with some name and description

/groupinfo - get info of your current group

/tests - get list of tests in your current group

/test [testId] - get info on some test, *

/edittest [testId] - get edit test menu, *

/ststfield [testId] [Field name] - set test field, *

/editquestions [testId] (not required)[offset] get edit questions menu with offset, *

/editquestion [questionId] get edit question menu, *

/addquestion [testId] add question to test, *

/starttest [testId] [numOfQuestion] - start taking the test, *

(* - called only from inline menu)
