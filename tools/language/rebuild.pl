#!/usr/bin/perl

use Getopt::Long;

my $original;
my $source;
my $destination;

my $lang;
my $path;

GetOptions ("language=s"   => \$lang,
            "path=s"  => \$path)
or die("Error in command line arguments\n");

if((!defined $path) || (!defined $lang)){
	print "Usage: rebuild.pl -l [language] -p [path]\n";
	print "\tlanguage: Idioma para remontar o arquivo XML (pasta language deve existir e conter translate_ok)\n";
	print "\tpath: PATH do diretorio RES do android\n";
	exit;
}

if(! -d $path){
	print "PATH ($path) invalido...\n";
	exit;
}

mkdir $path."values-".$lang;

$destination = $path."values-".$lang."/strings.xml";
$original = $lang."/translate_skeleton";
$source = $lang."/translate_ok";

open(R, $original);
@original = <R>;
close(R);

open(T,$source);
@translate = <T>;
close(T);

open(W,">$destination");

$i = 0;
foreach my $orig (@original){
	$translate[$i] =~ tr/\r\n//d;
	$orig =~ tr/\r\n//d;
	if($orig =~ s/>\d+</>$translate[$i]</g){
		print W $orig,"\n";
		$i++;
		next;
	}
	print W $orig,"\n";
}
close(W);
