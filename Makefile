JAVAC = javac
JAVA = java
SRC_DIR = src
OUT_DIR = out
COMMUNICATION_SERVER = CommunicationServer
ACCEPTOR_CLASS = Acceptor
PROPOSER_CLASS = Proposer

# Determine the appropriate path separator based on the OS
ifeq ($(OS), Windows_NT)
    PATH_SEPARATOR = ;
else
    PATH_SEPARATOR = :
endif

default: compile

compile:
	$(JAVAC) -d $(OUT_DIR) $(SRC_DIR)/*.java

server:
	$(JAVA) -cp $(OUT_DIR) $(COMMUNICATION_SERVER)

TARGETS_ACCEPTORS := M4 M5 M6 M7 M8 M9

$(TARGETS_ACCEPTORS): %:
	$(JAVA) -cp $(OUT_DIR) $(ACCEPTOR_CLASS) $@

acceptor:
	$(JAVA) -cp $(OUT_DIR) $(ACCEPTOR_CLASS) $(NAME)

UNIQUE_ID_M1 := 1
UNIQUE_ID_M2 := 2
UNIQUE_ID_M3 := 3
DELAY := 0

TARGETS_PROPOSERS := M1 M2 M3

$(TARGETS_PROPOSERS): %:
	$(JAVA) -cp $(OUT_DIR) $(PROPOSER_CLASS) $@ $(UNIQUE_ID_$@) $(DELAY)

proposer:
	$(JAVA) -cp $(OUT_DIR) $(PROPOSER_CLASS) $(NAME) $(UNIQUE_ID) $(DELAY)

.PHONY: test
TESTS := $(shell seq 1 10)

test:
	@for test in $(TESTS); do \
		bash test/test$$test.sh; \
		sleep 3; \
	done