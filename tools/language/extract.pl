#!/usr/bin/perl

use Getopt::Long;

my $original;
my $source;
my $destination;
my $lang;

GetOptions ("original=s" => \$original,
            "language=s"   => \$lang)
or die("Error in command line arguments\n");

if((!defined $original) || (!defined $lang)){
	print "Usage: extract.pl -o [orig] -l [language]\n";
	print "\torig: XML string original\n";
	print "\tlanguage: Qual a linguagem q será traduzido o original\n";
	exit;
}

$source = $lang."/translate_words";
$destination = $lang."/translate_skeleton";

mkdir $lang;

my @all = getOrig();
writeResult(@all);

sub getOrig(){
	local $/=">\n";
	open(R, $original);
	my @original = <R>;
	close(R);
	return @original;
}

sub writeResult(){
	@original = @_;

	open(T,">$source");
	open(W,">$destination");

	$i = 0;
	foreach my $orig (@original){
		$translate[$i] =~ tr/\r\n//d;
		$orig =~ tr/\r\n//d;
		if($orig =~ s/>(.*)</>$i</g){
			print T $1,"\n";
			$i++;
		}
		print W $orig,"\n";
	}
	close(T);
	close(W);
}
