# Build Your Own Compression Tool

Huffman Encoder/Decoder in Java following the [Coding
Challenges](https://codingchallenges.fyi/challenges/challenge-huffman) project.

## Commands

### Encode

- `encode`: Compresses an input file using Huffman encoding.
- `decode`: Decompresses a previously encoded file using Huffman decoding.

### Flags

- `--input`, `-i` <file>: Input file path
- `--output`, `-o` <file>: Output file path
- `--help`, `-h`: Show help message
- `--version`, `-v`: Print version information

## Usage

- Windows

```console
./mvnw.cmd clean package
./juffman.bat encode -i input.txt -o output.juf
./juffman.bat decode -i output.juf -o decoded.txt
```

- Unix / macOS

```console
./mvnw clean package
./juffman encode -i input.txt -o output.juf
./juffman decode -i output.juf -o decoded.txt
```

## File Format

Encoded files consist of:

1. Magic header (HUF)
2. Frequency table
3. Bitstream encoded using Huffman codes
