import sys
import random

try:
    opt = int(sys.argv[1])
    dim = int(sys.argv[2])

except:
    print 'Invalid args. Usage: python generate_matrices.py [1,2,-1] dimension'
    exit(0)


def print_matrices(a, b, d):
    f = open('random_matrices.txt', 'w')

    for i in range(2*(d**2)):
        f.write(str(random.randint(a, b)) + '\n')


def main():
    minimum = 0

    if opt == 1:
        print_matrices(minimum, 1, dim)

    elif opt == 2:
        print_matrices(minimum, 2, dim)

    elif opt == -1:
        minimum = opt
        maximum = 1
        print_matrices(minimum, maximum, dim)

if __name__ == "__main__":
    main()
