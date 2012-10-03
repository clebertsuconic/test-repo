test-repo
=========

Repository for several private tests I use


each directory here will be a different test


#test-filters-multipleMDBs


To run this test:

Setup JBOSS_HOME=/HOME-FOR-ANY-APPLICATION-SERVER, preferably JBoss7

run ant

deploy the ear under dist

And run the HornetQSender


The page system should be cleared at the end.

Notice that if you run it twice, you won't receive any more messages
