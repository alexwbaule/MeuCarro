#!/usr/bin/perl

$last_line;
$maior;

open(W,">models.xml");

print W qq^<?xml version="1.0" encoding="utf-8"?>
<resources>
	<string-array name="cars_models">\n^;

open(R,"IPVA.txt");
while($line = <R>){
	$line =~ tr/\r\n//d;
	next if ($line =~ /F\.PROPRIA/);
	next if ($line =~ /11\/11\/2013/);
	next if ($line =~ /Marca\/Modelo Comb/);
	next if ($line =~ / D | G | A /);
	next if ($line !~ /^\d+/);

	$line =~ /^\d+ (.*) [G|D|A]/;
	$this_line = $1;

	$this_line =~ s/\// /g;
	$this_line =~ s/\'/\\'/g;
	
	if($this_line ne $last_line){
		print W "\t\t<item>$this_line</item>\n";
	}
	$maior = $this_line if (length($this_line) > length($maior));
	$last_line = $this_line;
}
close(R);

print W qq^	</string-array>
</resources>^;

print "O MAIOR É $maior com ", length($maior)," caracteres\n";
